
    create table CourtCase (
        name varchar(255) not null,
        court varchar(255),
        disposition varchar(255),
        opinionDate date,
        publishDate date,
        summary varchar(4095),
        title varchar(255),
        primary key (name)
    );

    create table CourtCase_citations (
        CourtCase_name varchar(255) not null,
        code varchar(255),
        designated bool not null,
        refCount int4 not null,
        sectionNumber varchar(255)
    );

    alter table CourtCase_citations 
        add constraint FK415626813A25D08 
        foreign key (CourtCase_name) 
        references CourtCase;
