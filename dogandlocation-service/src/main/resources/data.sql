INSERT INTO location (location_id, name, shelter_type, address, city, province, country, postal_code, capacity,
                      available_space)
VALUES ('3e6c62a3-b1e7-4eb7-9642-8cdcb3ac74e6', 'Happy Tails Shelter', 'SHELTER', '123 Bark St', 'New York', 'New York', 'USA', '10001', 50, 10),
       ('9e7b4e90-21f0-42b6-94ce-62290dd444f4', 'Paws Haven', 'FOSTERHOME', '456 Woof Ave', 'Los Angeles', 'California', 'USA', '90012', 40, 5),
       ('73a41d3c-c22b-4fa9-bc79-5be64d06e315', 'Furry Friends Home', 'SHELTER', '789 Tail Rd', 'Chicago', 'Illinois', 'USA', '60605', 60, 12),
       ('4f2b77b1-70c8-4209-a4a7-b8c7b3eb6cb2', 'Safe Paws Rescue', 'MEDICALUNIT', '101 Fetch Blvd', 'Houston', 'Texas', 'USA', '77002', 45, 8),
       ('17999e90-bf87-4bc2-8d56-3e69d3b45715', 'Loving Paws Sanctuary', 'MEDICALUNIT', '202 Sniff St', 'Miami', 'Florida', 'USA', '33101', 55, 15),
       ('b5cb3e2d-57c5-44cf-8691-295574d23080', 'Golden Retriever Haven', 'MEDICALUNIT', '303 Wag Way', 'Denver', 'Colorado', 'USA', '80203', 35, 7),
       ('3ec65e2c-74d9-4b14-87ce-58e2b168c0c8', 'Puppy Love Shelter', 'MEDICALUNIT', '404 Playful Ct', 'Seattle', 'Washington', 'USA', '98104', 70,20),
       ('1d0d76ff-e4b2-4d9a-a6fc-e2b462e59b7f', 'Home for Hounds', 'MEDICALUNIT', '505 Loyal Ln', 'Boston', 'Massachusetts', 'USA', '02108', 30, 5),
       ('897fb4bc-9a99-4f8f-a149-fc359f10fd27', 'Rescue Haven', 'MEDICALUNIT', '606 Kindness Dr', 'San Francisco', 'California', 'USA', '94103', 50, 9),
       ('4ed9e4ef-5a34-4e7a-a685-82efac763a38', 'Adopt A Friend', 'MEDICALUNIT', '707 Forever Home St', 'Atlanta', 'Georgia', 'USA', '30303', 65, 18);

INSERT INTO dog (dog_id, name, breed, age, location_id, kennel_size, vaccination_status, availability_status)
VALUES ('2cfa25c5-1d13-4a9c-ae2a-55e2a5ae2481', 'Buddy', 'GERMAN_SHEPHERD', 3, '3e6c62a3-b1e7-4eb7-9642-8cdcb3ac74e6', 'LARGE', 'VACCINATED', 'AVAILABLE'),
       ('91d68bc9-909f-43ec-a354-bac620bdc32d', 'Bella', 'GERMAN_SHEPHERD', 4, '9e7b4e90-21f0-42b6-94ce-62290dd444f4', 'LARGE', 'VACCINATED', 'AVAILABLE'),
       ('4b290dc9-0152-4a30-991b-72c3e33e3f61', 'Charlie', 'GERMAN_SHEPHERD', 2, '73a41d3c-c22b-4fa9-bc79-5be64d06e315', 'LARGE', 'VACCINATED', 'ADOPTED'),
       ('a438f357-6c97-4c56-b4f3-5c17142dc301', 'Lucy', 'GERMAN_SHEPHERD', 5, '4f2b77b1-70c8-4209-a4a7-b8c7b3eb6cb2', 'MEDIUM', 'VACCINATED', 'AVAILABLE'),
       ('42f2088e-ec43-4030-bce9-243b1a86f8da', 'Max', 'GERMAN_SHEPHERD', 6, '17999e90-bf87-4bc2-8d56-3e69d3b45715', 'LARGE', 'NOT_VACCINATED', 'AVAILABLE'),
       ('26a12f28-6f42-4ef7-8ca6-48fe24602076', 'Daisy', 'GERMAN_SHEPHERD', 3, 'b5cb3e2d-57c5-44cf-8691-295574d23080', 'MEDIUM', 'VACCINATED', 'AVAILABLE'),
       ('e297cb14-788c-4208-b64d-24eb9a497d90', 'Rocky', 'GERMAN_SHEPHERD', 4, '3ec65e2c-74d9-4b14-87ce-58e2b168c0c8', 'LARGE', 'VACCINATED', 'ADOPTED'),
       ('8a589c97-8d43-4d2b-9d60-e7b14d0d2dcd', 'Milo', 'GERMAN_SHEPHERD', 2, '1d0d76ff-e4b2-4d9a-a6fc-e2b462e59b7f', 'LARGE', 'VACCINATED', 'AVAILABLE'),
       ('c91e8cc4-81c6-4044-9393-87248926f399', 'Sadie', 'GERMAN_SHEPHERD', 5, '897fb4bc-9a99-4f8f-a149-fc359f10fd27', 'SMALL', 'NOT_VACCINATED', 'AVAILABLE'),
       ('eb44d0a1-2512-4ff2-80f5-8c54ff24ea58', 'Duke', 'GERMAN_SHEPHERD', 3, '4ed9e4ef-5a34-4e7a-a685-82efac763a38', 'LARGE', 'VACCINATED', 'AVAILABLE');