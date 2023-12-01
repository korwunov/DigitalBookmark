package middleware

import (
	"fmt"
	"net/http"

	"pr9.com/m/data"
)

type fileInfo struct {
	_id        string
	chunkSize  int
	filename   string
	length     int
	uploadDate int
}

func GetFileById(fileId string, userId string, w http.ResponseWriter, r *http.Request) {
	isAllowed := isFileBelongsToUser(fileId, userId)
	fmt.Println(isAllowed)
	if isAllowed {
		buf := data.DownloadFile(fileId)
		if string(buf) == "not found" {
			w.WriteHeader(http.StatusNotFound)
		} else {
			w.WriteHeader(http.StatusOK)
			w.Write(buf)
		}
	} else {
		w.WriteHeader(http.StatusForbidden)
		w.Write([]byte("you are not allowed to download file of other user"))
	}

}

func AddFile(fileName string, w http.ResponseWriter, r *http.Request, userId string) {
	file, handler, err := r.FormFile(fileName)
	if err != nil {
		fmt.Println("Error Retrieving the File")
		fmt.Println(err)
		return
	}
	fmt.Println(userId)

	defer file.Close()
	fmt.Printf("Uploaded File: %+v\n", handler.Filename)
	fmt.Printf("File Size: %+v\n", handler.Size)
	fmt.Printf("MIME Header: %+v\n", handler.Header)

	buffer := make([]byte, handler.Size)
	file.Read(buffer)
	fileId := data.UploadFile(buffer, fileName)
	sendFileInfo("add", userId, fileId.Hex())
	w.WriteHeader(http.StatusOK)
	w.Write([]byte(fileId.Hex()))
}

func DeleteFile(fileId string, w http.ResponseWriter, r *http.Request, userId string) {
	isAllowed := isFileBelongsToUser(fileId, userId)
	fmt.Println(isAllowed)
	if isAllowed {
		data.DeleteFile(fileId)
		sendFileInfo("delete", userId, fileId)
		w.WriteHeader(http.StatusOK)
		w.Write([]byte("delete file with id " + fileId))
	} else {
		w.WriteHeader(http.StatusForbidden)
		w.Write([]byte("you are not allowed to delete file of other user"))
	}
}
