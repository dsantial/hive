-- Upgrade MetaStore schema from 2.1.1-cdh6.0.0 to 2.1.1-cdh6.1.0

-- HIVE-21077
ALTER TABLE DBS add CREATE_TIME NUMBER(10);

UPDATE CDH_VERSION SET SCHEMA_VERSION='2.1.1-cdh6.2.0', VERSION_COMMENT='Hive release version 2.1.1 for CDH 6.2.0' where VER_ID=1;
SELECT 'Finished upgrading MetaStore schema from 2.1.1-cdh6.1.0 to 2.1.1-cdh6.2.0' AS Status from dual;