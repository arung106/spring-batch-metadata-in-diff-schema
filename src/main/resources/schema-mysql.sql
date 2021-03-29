create table Customer (
       id bigint not null auto_increment,
        firstName varchar(255),
        lastName varchar(255),
		birthdate datetime,
        primary key (id)
    ) engine=InnoDB;