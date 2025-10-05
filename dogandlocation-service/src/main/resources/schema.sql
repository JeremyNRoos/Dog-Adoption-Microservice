drop table if exists location;
CREATE TABLE if not exists location
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    location_id     VARCHAR(1000) NOT NULL UNIQUE,
    name            VARCHAR(255)  NOT NULL,
    shelter_type    ENUM ('SHELTER', 'FOSTERHOME', 'MEDICALUNIT'),
    address         VARCHAR(255)  NOT NULL,
    city            VARCHAR(100)  NOT NULL,
    province        VARCHAR(50)   NOT NULL,
    country         VARCHAR(20)   NOT NULL,
    postal_code     varchar(45)   NOT NULL,
    capacity        INT           NOT NULL,
    available_space INT           NOT NULL
--                           kennel varchar(50)
);

drop table if exists dog;
CREATE TABLE if not exists dog
(
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    dog_id              VARCHAR(1000) NOT NULL UNIQUE,
    name                VARCHAR(255)  NOT NULL,
    breed               ENUM ('GERMAN_SHEPHERD','BULLDOG','LABRADOR_RETRIEVER','GOLDEN_RETRIEVER','SIBERIAN_HUSKY',
        'POODLE','CHIHUAHUA','DACHSHUND','PUG','ROTTWEILER','BORDER_COLLIE', 'BOXER','MALTESE','BICHON_FRISÉ',
        'AFGHAN_HOUND','CHOW_CHOW', 'POMERANIAN'),
    age                 INT           NOT NULL,
    location_id         VARCHAR(45)   NOT NULL,
    kennel_size         ENUM ('SMALL', 'MEDIUM', 'LARGE'),
    vaccination_status  enum ('VACCINATED', 'NOT_VACCINATED'),
    availability_status enum ('AVAILABLE', 'ADOPTED'),
    FOREIGN KEY (location_id) REFERENCES location (location_id)
);
