https://postgis.net/docs/reference.html#idm15481

select 'bron', b1.identificatie, o1.name, 'intersects', b2.identificatie, o2.name 
from bestuurlijkgebied b1, openbaarlichaam o1, bestuurlijkgebied b2, openbaarlichaam o2 
where b1.openbaarlichaam_id = o1.id and
b2.openbaarlichaam_id = o2.id and
ST_intersects(b1.geometry, b2.geometry) and
o1.name = 'Veenendaal';