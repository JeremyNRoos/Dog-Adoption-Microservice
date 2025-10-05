drop table if exists volunteer;
CREATE TABLE if not exists volunteer (
    id SERIAL,
    volunteer_id varchar(45) not null,
    f_name VARCHAR(45) not null,
    l_name VARCHAR(45) not null,
    email varchar(45) not null,
    salary decimal not null,
    title varchar(45) not null CHECK (title IN ('MANAGER', 'CLERK', 'VET')),
    type varchar(45) not null CHECK (type IN ('HOME', 'WORK', 'MOBILE')),
    phone_number varchar(45) not null,
    address varchar(45) not null,
    city varchar(45) not null,
    province varchar(45) not null,
    country varchar(45) not null,
    postal_code varchar(45) not null,
    PRIMARY KEY (id)
    );