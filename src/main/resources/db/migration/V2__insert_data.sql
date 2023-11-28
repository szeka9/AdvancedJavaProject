insert into toolpathsharing_user(email_address, name, role, password) values ('admin@toolpathsharinguser.local','admin', 'ROLE_SYSTEM_ADMIN', '\x2432612431302463754849496f3937666777346d37506457304164797554506655544448666858363833624f305639785749354b55446f6656344557');
insert into toolpathsharing_user(email_address, name, role, password) values ('kalman@oolpathsharinguser.local', 'kalman', 'ROLE_USER', '\x24326124313024555238544e4b41696d4877474a694f3568637a796d4f706a523963722f7030767943415530625138756648363069456678344c4f69');
insert into toolpathsharing_user(email_address, name, role, password) values ('eszter@oolpathsharinguser.local', 'eszter', 'ROLE_USER', '\x243279243130244b4432624f664875655265727974316e4f767173702e426e395961456c633446354c6b547576414266426f634853675544346a4661');


insert into machine(machine_type, workspace_depth, workspace_height, workspace_width, manufacturer, name, picture_uri) values (0, 444.5, 101.6, 444.5, 'Carbide 3D', 'Shapeoko 4', 'shapeoko-4.jpg');
insert into machine(machine_type, workspace_depth, workspace_height, workspace_width, manufacturer, name, picture_uri) values (1, 355.6, 0, 660.4, 'xTool', 'P2 laser cutter', 'xtool-p2.jpg');
insert into machine(machine_type, workspace_depth, workspace_height, workspace_width, manufacturer, name, picture_uri) values (2, 220.0, 250.0, 220.0, 'Creality', 'Ender 3', 'creality-ender-3.jpg');
insert into machine(machine_type, workspace_depth, workspace_height, workspace_width, manufacturer, name, picture_uri) values (2, 220.0, 300.0, 220.0, 'Creality', 'Ender 5', 'creality-ender-5.jpg');


insert into machinetool (user_id, name, supported_materials)
select id, '3.175mm Single Flute Router', '{wood, aluminium, acrylic}'
from toolpathsharing_user where name = 'kalman';

insert into machinetool (user_id, name, supported_materials)
select id, '6.35mm 4 Flute Router', '{steel, stainless steel, brass}'
from toolpathsharing_user where name = 'kalman';

insert into machinetool (user_id, name, supported_materials)
select id, 'Creality Sprite Extruder', '{PLA, PETG, TPU, HIPS, ABS, ASA}'
from toolpathsharing_user where name = 'eszter';

insert into machinetool (user_id, name, supported_materials)
select id, '20W laser diode', '{wood, leather, acrylic}'
from toolpathsharing_user where name = 'eszter';


insert into machine_machinetool (machine_id, machinetool_id)
select
    (select id from machine where name = 'Shapeoko 4'),
    (select id from machinetool where name = '3.175mm Single Flute Router');

insert into machine_machinetool (machine_id, machinetool_id)
select
    (select id from machine where name = 'Shapeoko 4'),
    (select id from machinetool where name = '6.35mm 4 Flute Router');

insert into machine_machinetool (machine_id, machinetool_id)
select
    (select id from machine where name = 'P2 laser cutter'),
    (select id from machinetool where name = '20W laser diode');

insert into machine_machinetool (machine_id, machinetool_id)
select
    (select id from machine where name = 'Ender 3'),
    (select id from machinetool where name = 'Creality Sprite Extruder');

insert into machine_machinetool (machine_id, machinetool_id)
select
    (select id from machine where name = 'Ender 5'),
    (select id from machinetool where name = 'Creality Sprite Extruder');
