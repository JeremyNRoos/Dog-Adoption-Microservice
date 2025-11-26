drop table if exists adopter;
create table if not exists adopter
(
    id                        Int AUTO_INCREMENT PRIMARY KEY,
    adopter_id                varchar(45) not null UNIQUE,
    f_name                    varchar(45) not null,
    l_name                    varchar(45) not null,
    address                   varchar(45) not null,
    city                      varchar(45) not null,
    province                  varchar(45) not null,
    country                   varchar(45) not null,
    postal_code               varchar(45),
    type                      enum ('HOME', 'WORK', 'MOBILE'),
    phone_number              varchar(45),
    contact_method_preference enum ('EMAIL', 'PHONE', 'TEXT')
    );