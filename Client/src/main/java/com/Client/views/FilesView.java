package com.Client.views;

import com.Client.model.UserSession;
import com.Client.model.response.FileDTO;
import com.Client.model.response.ShortenUserDataResponse;
import com.Client.model.response.UserDataDTO;
import com.Client.services.FilesService;
import com.Client.services.UsersService;
import com.Client.views.components.Header;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@Route("files")
@PageTitle("Файлы")
@CssImport("./styles/files_view_style.css")
public class FilesView extends VerticalLayout implements BeforeEnterObserver {
    private FilesService filesService;
    private UsersService usersService;

    private Div mainContainer = new Div();
    private Grid<FileDTO> userFilesGrid = new Grid<>(FileDTO.class);
    private Grid<FileDTO> accessibleFilesGrid = new Grid<>(FileDTO.class);
    private ConfirmDialog confirmDialog = new ConfirmDialog();
    private Dialog shareFileDialog = new Dialog();
    private ComboBox<ShortenUserDataResponse> userComboBox = new ComboBox<>("Выберите пользователя");
    private Button shareFileButton = new Button("Поделиться");
    private Dialog uploadFileDialog = new Dialog();
    private Button uploadButton = new Button("Загрузить файл");
    private MultiFileBuffer buffer = new MultiFileBuffer();
    private Upload upload = new Upload(buffer);

    private Registration shareFileButtonListener;
    private Registration deleteFileListener;

    public FilesView(FilesService filesService, UsersService usersService) {
        this.filesService = filesService;
        this.usersService = usersService;

        //Настройка грида для файлов пользователя
        userFilesGrid.removeAllColumns();
        userFilesGrid.addClassName("grid-elem");
        userFilesGrid.addColumn(FileDTO::getFileName).setHeader("Название файла");
        userFilesGrid.addColumn(file -> formatSize(file.getFileSize())).setHeader("Размер");
        userFilesGrid.addComponentColumn(file -> {
            Anchor downloadAnchor = downloadFile(file.getId());

            Icon deleteIcon = new Icon(VaadinIcon.TRASH);
            deleteIcon.addClickListener(e -> openDeleteFileDialog(file.getId()));
            deleteIcon.getElement().getStyle().set("color", "red").set("cursor", "pointer");
            deleteIcon.addClassName("delete-button");

            Icon shareIcon = new Icon(VaadinIcon.SHARE);
            shareIcon.addClickListener(e -> openShareFileDialog(file.getId()));
            shareIcon.getElement().getStyle().set("color", "#28a745").set("cursor", "pointer");
            shareIcon.addClassName("share-button");

            HorizontalLayout actionsLayout = new HorizontalLayout(downloadAnchor, shareIcon, deleteIcon);
            actionsLayout.setSpacing(true);
            return actionsLayout;
        }).setHeader("Действия");

        // Настройка грида для доступных файлов
        accessibleFilesGrid.removeAllColumns();
        accessibleFilesGrid.addClassName("grid-elem");
        accessibleFilesGrid.addColumn(FileDTO::getFileName).setHeader("Название файла");
        accessibleFilesGrid.addColumn(file -> formatSize(file.getFileSize())).setHeader("Размер");
        accessibleFilesGrid.addComponentColumn(file -> {
            Anchor downloadAnchor = downloadFile(file.getId());
            HorizontalLayout actionsLayout = new HorizontalLayout(downloadAnchor);
            actionsLayout.setSpacing(true);
            return actionsLayout;
        }).setHeader("Действия");

        // Настройка диалогового окна для подтверждения удаления файла
        confirmDialog.setHeader("Подтверждение");
        confirmDialog.setText("Вы уверены, что хотите удалить файл?");
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmText("Подтвердить");
        confirmDialog.setCancelText("Отмена");

        // Настройка диалогового окна для предоставления доступа к файлу
        userComboBox.setItemLabelGenerator(ShortenUserDataResponse::getName);
        userComboBox.getStyle().set("width", "70%").set("margin-right", "5%");
        H2 shareFileDialogTitle = new H2("Форма для предоставления доступа к файлу");
        Icon shareFileDialogCloseButton = new Icon(VaadinIcon.CLOSE_SMALL);
        shareFileDialogCloseButton.getElement().getStyle().set("cursor", "pointer");
        shareFileDialogCloseButton.addClickListener(e -> shareFileDialog.close());

        HorizontalLayout shareFileDialogHeaderLayout = new HorizontalLayout(shareFileDialogTitle, shareFileDialogCloseButton);
        shareFileDialogHeaderLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        shareFileDialogHeaderLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        shareFileDialogHeaderLayout.setWidthFull();
        shareFileDialogHeaderLayout.setPadding(true);
        shareFileDialogHeaderLayout.addClassName("dialog-header");

        shareFileDialog.add(shareFileDialogHeaderLayout, userComboBox, shareFileButton);

        // Настройка компонента загрузки файла
//        upload.setAcceptedFileTypes("*");
        upload.setDropAllowed(true);
        upload.setReceiver(buffer);
        upload.setMaxFileSize(10 * 1024 * 1024); // 10 MB
        upload.addSucceededListener(event -> {
            byte[] fileData = null;
            String fileName = null;
            try {
                fileName = event.getFileName();
                fileData = buffer.getInputStream(fileName).readAllBytes();
            } catch (IOException e) {
                Notification.show("Ошибка при чтении файла", 3000, Notification.Position.TOP_CENTER);
            }

            if (fileData != null && fileName != null) {
                try {
                    filesService.uploadFile(fileData, fileName);
                    Notification.show("Файл загружен успешно", 3000, Notification.Position.TOP_CENTER);
                    refreshGrids();
                } catch (Exception e) {
                    Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
                }
            }
        });
        upload.addFailedListener(event -> {
            Notification.show("Ошибка при загрузке файла: " + event.getReason(), 3000, Notification.Position.TOP_CENTER);
        });

        // Настройка формы для загрузки файла
        H2 uploadDialogTitle = new H2("Загрузка файла");
        Icon uploadDialogCloseButton = new Icon(VaadinIcon.CLOSE_SMALL);
        uploadDialogCloseButton.getElement().getStyle().set("cursor", "pointer");
        uploadDialogCloseButton.addClickListener(e1 -> uploadFileDialog.close());

        HorizontalLayout uploadDialogHeaderLayout = new HorizontalLayout(uploadDialogTitle, uploadDialogCloseButton);
        uploadDialogHeaderLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        uploadDialogHeaderLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        uploadDialogHeaderLayout.setWidthFull();
        uploadDialogHeaderLayout.setPadding(true);
        uploadDialogHeaderLayout.addClassName("dialog-header");

        uploadFileDialog.add(uploadDialogHeaderLayout, upload);
        upload.addClassName("upload-component");

        uploadButton.addClickListener(e -> {
            uploadFileDialog.open();
        });
        uploadButton.addClassName("upload-button");

        // Настройка основного содержимого страницы
        Div gridsContainer = new Div();
        gridsContainer.setClassName("grids-container");
        gridsContainer.add(userFilesGrid, accessibleFilesGrid);

        Div mainContainerHeader = new Div();
        mainContainerHeader.add(new H2("Файлы"), uploadButton);
        mainContainerHeader.setClassName("grid-header");
        mainContainer.setClassName("files-view");
        mainContainer.add(mainContainerHeader, gridsContainer);
        setClassName("background-style");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (UserSession.getUserData() == null) {
            event.rerouteTo("/login");
        }
        else {
            add(new Header(FilesView.class, UserSession.getUserRole()), mainContainer);
            refreshGrids();
        }
    }

    private void openDeleteFileDialog(Long fileId) {
        if (Objects.nonNull(this.deleteFileListener)) deleteFileListener.remove();
        this.deleteFileListener = confirmDialog.addConfirmListener(e -> deleteFile(fileId));
        confirmDialog.open();
    }

    private void refreshGrids() {
        // Загружаем файлы пользователя
        List<FileDTO> userFiles = filesService.getAllUsersFiles(false);
        userFilesGrid.getDataProvider().refreshAll();
        userFilesGrid.setItems(userFiles);

        // Загружаем доступные файлы
        List<FileDTO> accessibleFiles = filesService.getAllUsersFiles(true);
        accessibleFilesGrid.getDataProvider().refreshAll();
        accessibleFilesGrid.setItems(accessibleFiles);

        loadUsersForSharing();
    }

    private void openShareFileDialog(Long fileId) {
        if (Objects.nonNull(shareFileButtonListener)) shareFileButtonListener.remove();
        shareFileButtonListener = shareFileButton.addClickListener(e -> shareFile(fileId));
        shareFileDialog.open();
    }

    private Anchor downloadFile(Long fileId) {
        Resource fileResource;
        try {
            fileResource = filesService.downloadFile(fileId);
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            return null;
        }
        if (fileResource != null) {
            try {
                InputStream inputStream = fileResource.getInputStream();
                StreamResource resource = new StreamResource(fileResource.getFilename(), () -> inputStream);
                Anchor downloadAnchor = new Anchor(resource, "Скачать");
                downloadAnchor.getElement().getStyle().set("color", "#007bff").set("cursor", "pointer");
                downloadAnchor.addClassName("download-button");
                downloadAnchor.setTarget("_blank");
                return downloadAnchor;
            } catch (IOException e) {
                Notification.show("Не удалось загрузить файл", 3000, Notification.Position.TOP_CENTER);
                return null;
            }
        } else {
            return new Anchor("#", "Файл не найден");
        }
    }

    private void loadUsersForSharing() {
        Long currentUserId = UserSession.getUserData().id;
        List<ShortenUserDataResponse> users = usersService.getShortenUserData().stream().filter(elem -> !Objects.equals(elem.id, currentUserId)).toList();
        userComboBox.setItems(users);
    }

    private void deleteFile(Long fileId) {
        try {
            filesService.deleteFile(fileId);
            Notification.show("Файл удален успешно", 3000, Notification.Position.TOP_CENTER);
            refreshGrids();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void shareFile(Long fileId) {
        Long userIdToShare = userComboBox.getValue().id;
        try {
            filesService.shareFile(fileId, userIdToShare);
            Notification.show("Доступ для пользователя " + userComboBox.getValue().name + " предоставлен успешно", 3000, Notification.Position.TOP_CENTER);
            shareFileDialog.close();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private String formatSize(long size) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double formattedSize = size;

        while (formattedSize >= 1024 && unitIndex < units.length - 1) {
            formattedSize /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", formattedSize, units[unitIndex]);
    }
}
