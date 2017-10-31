
    alter table CourtCase_caseCitations 
        drop constraint FK3cidyv9q2y9vo8tuirudst1kb;

    alter table CourtCase_codeCitations 
        drop constraint FKnlpodr5mmjel2w9f89wv2rjbo;

    drop table if exists account cascade;

    drop table if exists CourtCase cascade;

    drop table if exists CourtCase_caseCitations cascade;

    drop table if exists CourtCase_codeCitations cascade;

    drop sequence hibernate_sequence;

    create sequence hibernate_sequence start 1 increment 1;

    create table account (
        id int8 not null,
        codes bytea,
        createDate timestamp,
        email varchar(255),
        emailUpdates boolean not null,
        locale varchar(255),
        password varchar(255),
        role varchar(255),
        updateDate timestamp,
        verified boolean not null,
        verifyCount int4 not null,
        verifyErrors int4 not null,
        verifyKey varchar(255),
        primary key (id)
    );

    create table CourtCase (
        id int8 not null,
        court varchar(31) not null,
        defaultCodeSection varchar(255),
        disposition varchar(31),
        extension varchar(31) not null,
        name varchar(31) not null,
        opinionDate date,
        publishDate date,
        summary varchar(4095),
        title varchar(255) not null,
        primary key (id)
    );

    create table CourtCase_caseCitations (
        CourtCase_id int8 not null,
        page int4 not null,
        volume int4 not null,
        vset int4 not null
    );

    create table CourtCase_codeCitations (
        CourtCase_id int8 not null,
        code varchar(255),
        designated boolean not null,
        refCount int4 not null,
        sectionNumber varchar(127) not null
    );

    alter table account 
        add constraint UK_q0uja26qgu1atulenwup9rxyr unique (email);

    alter table CourtCase 
        add constraint UK_iyt0d3n5ppbr8yqcolk2fx7f9 unique (name);

    alter table CourtCase_caseCitations 
        add constraint FK3cidyv9q2y9vo8tuirudst1kb 
        foreign key (CourtCase_id) 
        references CourtCase;

    alter table CourtCase_codeCitations 
        add constraint FKnlpodr5mmjel2w9f89wv2rjbo 
        foreign key (CourtCase_id) 
        references CourtCase;
