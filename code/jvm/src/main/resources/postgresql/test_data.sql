insert into "User" values
                       (1,'AstroFredy',''),
                       (2,'Mike',''),
                       (3,'Matilde',''),
                       (4,'PurplePapi',''),
                       (5,'Biou','');

insert into gamerules values
                          (1, 10, 3, 2000, 2000),
                          (2, 10, 1, 2000, 2000);

insert into shiprules values
                                 ('Russian', 2,2,1),
                                 ('Russian', 3,1,1),
                                 ('Russian', 5,1,1),
                                 ('Russian', 4,1,1),
                                 ('USN', 3,2,2),
                                 ('USN', 2,1,2),
                                 ('USN', 4,1,2),
                                 ('USN', 7,1,2);

insert into game values
                     (1, 1, 'Finished', 2),
                     (2, 1, 'Running',  3),
                     (3, 2, 'Finished',  5),
                     (4, 1, 'Finished',  4),
                     (5, 2, 'Running',  2),
                     (6, 2, 'Running',  2),
                     (7, 1, 'Finished', 5),
                     (8, 2, 'Running',  4),
                     (9, 1, 'Finished', 3);

insert into board values
                      ('####BB##B##B', 1,2),
                      ('####BBBBB##B', 1,1),
                      ('#B##BB##B##B', 2,2),
                      ('##BBBB##BB#B', 2,3),
                      ('####BB##B##B', 3,2),
                      ('####BBB#B##B', 3,5),
                      ('####BBBBBB#B', 4,1),
                      ('####BB#BB##B', 4,4),
                      ('BB##BB##B##B', 5,1),
                      ('####BB##B##B', 5,2),
                      ('####BBBB###B', 6,3),
                      ('#B##BB##B##B', 6,2),
                      ('B#BBBB##BB#B', 7,1),
                      ('##BBBBBBB##B', 7,2),
                      ('####BBB#B##B', 8,3),
                      ('B###BBBBBB#B', 8,4),
                      ('#BBBBB#BB##B', 9,3),
                      ('BBB#BB##B##B', 9,2);

insert into systeminfo values
                           ('0.0.1');

insert into authors values('Tiago Filipe', '48265@alunos.isel.pt','https://github.com/TiagoJFil'),
                           ('Francisco Costa', '48282@alunos.isel.pt','https://github.com/franciscocostalap'),
                           ('Teodosie Pienescu','a48267@alunos.isel.pt','https://github.com/TeoPienescu');