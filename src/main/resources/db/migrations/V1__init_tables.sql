create table if not exists marks (
        mark_record_id bigint not null,
        mark_giver_id bigint,
        mark_owner_id bigint,
        mark_subject_id bigint,
        primary key (mark_record_id)
);

create table if not exists subjects (
        id bigint not null,
        name varchar(255) not null,
        primary key (id)
);

create table if not exists teachers (
        id bigint not null,
        email varchar(255) not null,
        name varchar(255) not null,
        primary key (id)
);

create table if not exists students (
        id bigint not null,
        email varchar(255) not null,
        name varchar(255) not null,
        primary key (id)
);

alter table if exists marks
       add constraint FKrvvuc7thev70n9a8f07hwkmjk
       foreign key (mark_giver_id)
       references teachers
       on delete cascade;

alter table if exists marks
       add constraint FK9ohntgwuj5ls7x2e5rlftk1p4
       foreign key (mark_owner_id)
       references students
       on delete cascade;

alter table if exists marks
       add constraint FKb4vl4xr62rd6hf4secyrfl025
       foreign key (mark_subject_id)
       references subjects
       on delete cascade;
