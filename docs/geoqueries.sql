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