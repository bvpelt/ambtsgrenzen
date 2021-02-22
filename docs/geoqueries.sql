-- reference: https://postgis.net/docs/reference.html#idm15481

select 'bron', b1.identificatie, o1.name, 'intersects', b2.identificatie, o2.name
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        o1.name = 'Veenendaal' and
        b1.id <> b2.id;

-- buffering shrink with negative parameter
select ST_astext(ST_buffer(b.geometry,-2))
from bestuurlijkgebied b, openbaarlichaam o
where b.openbaarlichaam_id = o.id and
        o.name = 'Veenendaal';

select ST_astext(ST_buffer(b.geometry,-2))
from bestuurlijkgebied b, openbaarlichaam o
where b.openbaarlichaam_id = o.id and
        o.name = 'Baarle-Nassau';

-- geometry validation
select ST_IsValid(b.geometry), ST_IsValidReason(b.geometry)
from bestuurlijkgebied b, openbaarlichaam o
where b.openbaarlichaam_id = o.id and
        o.name = 'Baarle-Nassau';


-- test geometry
-- shrink veenendaal
delete from bestuurlijkgebied where id = 400;
delete from openbaarlichaam where id = 400;

insert into openbaarlichaam (id, code, type, name) values (400, 'st_buffer -2', 'Gemeente', 'st_buffer -2');

insert into bestuurlijkgebied (id, identificatie, domein, type, openbaarlichaam_id, geometry )
select 400, 'st_buffer -2', 'NL.BI.BestuurlijkGebied', 'territoriaal', 400, ST_buffer(x.geometry,-2) from bestuurlijkgebied x where id = 98;

-- controle
select 'bron', b1.identificatie, o1.name, 'intersects', b2.identificatie, o2.name
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        b1.id = 400 and
        b1.id <> b2.id;

-- baarle-nassau test
delete from bestuurlijkgebied where id = 401;
delete from openbaarlichaam where id = 401;

insert into openbaarlichaam (id, code, type, name) values (401, 'stbuffer 401', 'Gemeente', 'stbuffer 401');

insert into bestuurlijkgebied (id, identificatie, domein, type, openbaarlichaam_id, geometry )
select 401, 'stbuffer 401', 'NL.BI.BestuurlijkGebied', 'territoriaal', 401, ST_buffer(x.geometry,-1) from bestuurlijkgebied x where id = 199;
select * from bestuurlijkgebied where id in (199,401);

select 'bron', b1.identificatie, o1.name, 'intersects', b2.identificatie, o2.name
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        o1.name = 'Baarle-Nassau' and
        b1.id <> b2.id;

select 'bron', b1.identificatie, o1.name, 'intersects', b2.identificatie, o2.name
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        b1.id = 401 and
        b1.id <> b2.id;


CREATE INDEX idx_geometry ON public.bestuurlijkgebied USING gist (geometry);
DROP INDEX idx_geometry;

-- met index: 14.476 seconden
-- zonder index 41.81 seconden
select 'bron', b1.identificatie, o1.name, 'intersects', b2.identificatie, o2.name
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        b1.id <> b2.id;

-- Perceel at border of Zaltbommel
insert into openbaarlichaam (id, code, type, name) values (500, 'perceel 500', 'Perceel', 'perceel 500');

insert into bestuurlijkgebied (id, identificatie, domein, type, openbaarlichaam_id, geometry )
values (500, 'KWK02 L 274', 'Perceel', 'territoriaal', 500,
        ST_GeometryFromText('POLYGON((141019.113 417056.301, 141082.863 417041.791, 141073.677 417132.101, 141036.451 417138.578, 141007.173 417145.909, 140990.686 417149.43, 140950.198 417157.788, 140918.127 417164.489, 140879.743 417172.47, 140875.637 417173.321, 140845.368 417179.595, 140773.109 417194.658, 140742.941 417201.129, 140700.997 417210.022, 140642.339 417220.331, 140592.044 417229.332, 140569.112 417232.382, 140522.801 417238.226, 140468.28 417245.079, 140431.951 417247.982, 140352.436 417254.311, 140341.725 417254.854, 140288.404 417257.559, 140211.015 417261.461, 140164.692 417263.853, 140069.217 417261.904, 140004.078 417261.139, 139946.743 417259.959, 139893.429 417256.872, 139841.767 417253.554, 139752.123 417244.016, 139640.799 417237.102, 139634.71 417147.395, 139699.913 417157.357, 139799.993 417167.13, 139845.207 417170.049, 139900.962 417173.475, 139999.264 417175.247, 140099.599 417175.242, 140115.856 417174.987, 140200.188 417172.316, 140300.142 417168.12, 140331.875 417166.435, 140400.615 417161.99, 140500.279 417154.713, 140599.064 417142.884, 140699.548 417127.962, 140800.013 417107.323, 140817.184 417103.195, 141019.113 417056.301))', 28992));

select * from bestuurlijkgebied b, openbaarlichaam o where b.openbaarlichaam_id = o.id and o.name = 'Zaltbommel';
select * from bestuurlijkgebied where id in (79,500);

-- found (64 Maasdriel -, 79 Zalbommel +, 358 Gelderland +, 363 Noord-Brabant -, 371 Waterschap Rivierenland +, 379 Waterschap Aa en Maas -, 218 Heusden -, 365 Nederland +)
select 'bron', b1.identificatie, o1.name, 'intersects', b2.id, b2.identificatie, o2.name, b2.geometry
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        b1.id = 500 and
        b1.id <> b2.id;

-- view areas of interrest by changing the query
select * from bestuurlijkgebied b, openbaarlichaam o where o.id=b.id and b.id in (500,79,379); --(500,64,79,358,363,371,379,218)

insert into openbaarlichaam (id, code, type, name) values (501, 'perceel -1', 'Gemeente', 'perceel -1');

insert into bestuurlijkgebied (id, identificatie, domein, type, openbaarlichaam_id, geometry )
select 501, 'KWK02 L 274 --', 'NL.BI.BestuurlijkGebied', 'territoriaal', 501, ST_buffer(x.geometry,-1) from bestuurlijkgebied x where id = 500;

-- found (500 - old perceel +, 79 Zaltbommel +, 358 Gelderland +, 371 Waterschap Rivierenland +, 365 Nederland +)
select 'bron', b1.identificatie, o1.name, 'intersects', b2.id, b2.identificatie, o2.name, b2.geometry
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        b1.id = 501 and
        b1.id <> b2.id;
                            
                           
-- Testsets
--                          
delete from bestuurlijkgebied where id > 390;
delete from openbaarlichaam where id > 390;

-- toon alle gemeenten in nederland
select b.*, o.* from bestuurlijkgebied b, openbaarlichaam o where b.openbaarlichaam_id = o.id and b.id < 390 and b.identificatie like 'GM%';

-- toon gemeente zaltbommel
select b.*, o.* from bestuurlijkgebied b, openbaarlichaam o where b.openbaarlichaam_id = o.id and o.name = 'Zaltbommel';

--
-- bepaal welke bestuurlijke gezagen een intersect hebben met gemeente Zaltbommel
--
select 'bron', b1.id, b1.identificatie, o1.name, 'intersects', b2.id, b2.identificatie, o2.name, b2.geometry
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        o1.name = 'Zaltbommel' and
        b1.id <> b2.id and
		b2.id < 390;
		
-- 
-- normale dataset ids 0..389
--
-- id 400 - gemeente zaltbommel -1 meter
-- id 500 - perceel 500 aan de rand van zaltbommel
-- id 600 - perceel 500 -1 meter

--
-- insert gebied zaltbommel met verkleinde geometry met id 400
--
delete from openbaarlichaam where id = 400;
delete from bestuurlijkgebied where id = 400;
insert into openbaarlichaam (id, code, type, name) values (400, 'buffer -1', 'Gemeente', 'Zaltbommel -1');
insert into bestuurlijkgebied (id, identificatie, domein, type, openbaarlichaam_id, geometry )
select 400, 'buffer -1', 'NL.BI.BestuurlijkGebied', 'territoriaal', 400, ST_buffer(x.geometry, -0.001) from bestuurlijkgebied x where id = 79;

select b.*, o.* from bestuurlijkgebied b, openbaarlichaam o where b.openbaarlichaam_id = o.id and b.id in (79,400);
--
-- bepaal welke bestuurlijke gezagen een intersect hebben met gemeente Zaltbommel -1
--
select 'bron', b1.id, b1.identificatie, o1.name, 'intersects', b2.id, b2.identificatie, o2.name, b2.geometry
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        o1.name = 'Zaltbommel -1' and
        b1.id <> b2.id and
		b2.id < 500;

--
-- Via de viewer bepaald dat perceel KWK02 L 274 op de grens van Zaltbommel ligt
-- Insert Perceel op de grens van Zaltbommel met id 500
--
insert into openbaarlichaam (id, code, type, name) values (500, 'perceel 500', 'Perceel', 'perceel 500');

insert into bestuurlijkgebied (id, identificatie, domein, type, openbaarlichaam_id, geometry )
values (500, 'KWK02 L 274', 'Perceel', 'territoriaal', 500,
        ST_GeometryFromText('POLYGON((141019.113 417056.301, 141082.863 417041.791, 141073.677 417132.101, 141036.451 417138.578, 141007.173 417145.909, 140990.686 417149.43, 140950.198 417157.788, 140918.127 417164.489, 140879.743 417172.47, 140875.637 417173.321, 140845.368 417179.595, 140773.109 417194.658, 140742.941 417201.129, 140700.997 417210.022, 140642.339 417220.331, 140592.044 417229.332, 140569.112 417232.382, 140522.801 417238.226, 140468.28 417245.079, 140431.951 417247.982, 140352.436 417254.311, 140341.725 417254.854, 140288.404 417257.559, 140211.015 417261.461, 140164.692 417263.853, 140069.217 417261.904, 140004.078 417261.139, 139946.743 417259.959, 139893.429 417256.872, 139841.767 417253.554, 139752.123 417244.016, 139640.799 417237.102, 139634.71 417147.395, 139699.913 417157.357, 139799.993 417167.13, 139845.207 417170.049, 139900.962 417173.475, 139999.264 417175.247, 140099.599 417175.242, 140115.856 417174.987, 140200.188 417172.316, 140300.142 417168.12, 140331.875 417166.435, 140400.615 417161.99, 140500.279 417154.713, 140599.064 417142.884, 140699.548 417127.962, 140800.013 417107.323, 140817.184 417103.195, 141019.113 417056.301))', 28992));

select b.*, o.* from bestuurlijkgebied b, openbaarlichaam o where b.openbaarlichaam_id = o.id and b.id in (79,500);

--
-- Welke intersect is er met perceel 500
--
select 'bron', b1.id, b1.identificatie, o1.name, 'intersects', b2.id, b2.identificatie, o2.name, b2.geometry
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        o1.name = 'perceel 500' and
        b1.id <> b2.id and
		b2.id NOT in (400, 600);
		
--
-- Insert perceel 500 -1 meter met id 600
--
insert into openbaarlichaam (id, code, type, name) values (600, 'perc -1', 'Perceel', 'Perceel 500 -1');
insert into bestuurlijkgebied (id, identificatie, domein, type, openbaarlichaam_id, geometry )
select 600, 'perc -1', 'NL.BI.BestuurlijkGebied', 'territoriaal', 600, ST_buffer(x.geometry, -1) from bestuurlijkgebied x where id = 500;

select b.*, o.* from bestuurlijkgebied b, openbaarlichaam o where b.openbaarlichaam_id = o.id and b.id in (500,600);

select 'bron', b1.id, b1.identificatie, o1.name, 'intersects', b2.id, b2.identificatie, o2.name, b2.geometry
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2
where b1.openbaarlichaam_id = o1.id and
        b2.openbaarlichaam_id = o2.id and
    ST_intersects(b1.geometry, b2.geometry) and
        o1.name = 'Perceel 500 -1' and
        b1.id <> b2.id and
		b2.id NOT in (400, 500);                            
                            
