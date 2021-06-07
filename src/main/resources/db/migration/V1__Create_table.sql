  
CREATE SCHEMA IF NOT EXISTS userdb;

GRANT USAGE ON SCHEMA userdb to rss_syncer;

CREATE TABLE IF NOT EXISTS userdb.postnotifer(
    Id SERIAL PRIMARY KEY,
    url TEXT
);
