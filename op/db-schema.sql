
    alter table CourtCase_caseCitations 
        drop constraint FK_rvp370291erg8x0jgve3vn05h;
    alter table CourtCase_codeCitations 
        drop constraint FK_rsh3r22xieaviae2yx7wktixk;
    drop table if exists CourtCase cascade;
    drop table if exists CourtCase_caseCitations cascade;
    drop table if exists CourtCase_codeCitations cascade;
    drop table if exists account cascade;
    drop sequence hibernate_sequence;
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
    alter table CourtCase 
        add constraint UK_iyt0d3n5ppbr8yqcolk2fx7f9 unique (name);
    alter table account 
        add constraint UK_q0uja26qgu1atulenwup9rxyr unique (email);
    alter table CourtCase_caseCitations 
        add constraint FK_rvp370291erg8x0jgve3vn05h 
        foreign key (CourtCase_id) 
        references CourtCase;
    alter table CourtCase_codeCitations 
        add constraint FK_rsh3r22xieaviae2yx7wktixk 
        foreign key (CourtCase_id) 
        references CourtCase;
    create sequence hibernate_sequence;