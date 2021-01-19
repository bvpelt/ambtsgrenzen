# Tips

## Maven
### Show dependencies
See https://javadeveloperzone.com/maven/maven-show-dependency-tree/
```bash
$ mvn dependency:tree
$ mvn dependency:tree -Dverbose
```
## GeoJson
See 
- https://gis.stackexchange.com/questions/130913/geojson-java-library
- google gson  https://github.com/google/gson

## Postgres
- Create spatial database https://www.techiediaries.com/postgresql-postgis-tutorial/
- Postgis http://postgis.net/docs/manual-3.1/

### Create database
Become user postgres and create the database
```shell
sudo su - postgres
createdb ambtsdb
```
### Add postgis extension
As postgres user
```shell
psql ambtsdb
```
When postgres starts:
```sql
create extension postgis;
create extension postgis_topology;
```
### Create user
As postgres user
```shell
createuser testuser
```
### Grant access
As user postgres open database and grant access
```shell
psql ambtsdb
```
When postgres starts:
```sql
grant all privileges on database ambtsdb to bvpelt;
grant all privileges on database ambtsdb to testuser;
```
### Get access
```shell
psql -h localhost -d ambtsdb 
psql -h localhost -d ambtsdb --username testuser
```
