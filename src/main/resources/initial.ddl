DELIMITER $
-- /p chenrizhang 2019-10-25 存储过程，获取索引
DROP PROCEDURE IF EXISTS GETINDEXTYPE;
$
CREATE PROCEDURE GETINDEXTYPE(IN piUniqueStr varchar(64), OUT indextype int)
BEGIN
    DECLARE vPos int;
    DECLARE vStr varchar(64);
    DECLARE vStr2 varchar(32);
    SET vStr = upper(ltrim(rtrim(piUniqueStr)));
    SET vPos = INSTR(vStr, ' ');
    IF vPos = 0
    THEN
        IF vStr = 'INDEX'
        THEN
            SET indextype = 0;
        ELSEIF vStr = 'CHECK'
        THEN
            SET indextype = 4;
        ELSEIF vStr = 'UNIQUE'
        THEN
            SET indextype = 5;
        ELSE
            SET indextype = 0;
        END IF;
    ELSE
        SET vStr2 = ltrim(rtrim(substring(vStr, vPos + 1, LENGTH(vStr) - vPos)));
        SET vStr = ltrim(rtrim(substring(vStr, 1, vPos - 1)));
        IF vStr = 'UNIQUE' AND vStr2 = 'INDEX'
        THEN
            SET indextype = 1;
        ELSEIF vStr = 'PRIMARY' AND vStr2 = 'KEY'
        THEN
            SET indextype = 2;
        ELSEIF vStr = 'FOREIGN' AND vStr2 = 'KEY'
        THEN
            SET indextype = 3;
        ELSE
            SET indextype = 0;
        END IF;
    END IF;
END;
$
-- /p chenrizhang 2019-10-25 存储过程，创建索引
DROP PROCEDURE IF EXISTS CREATEINDEX;
$
CREATE PROCEDURE CREATEINDEX(IN piIndexName varchar(100), IN piTableName varchar(100), IN piColNames varchar(1024), IN piUniqueStr varchar(64))
BEGIN
    DECLARE ColNames varchar(1024);
    DECLARE ClusterStr varchar(20);
    DECLARE piUnique int;
    CALL GETINDEXTYPE(piUniqueStr, piUnique);
    SET ColNames = ltrim(rtrim(piColNames));
    IF (SubString(ColNames, 1, 1) <> '(')
    THEN
        SET ColNames = CONCAT('(', ColNames, ')');
    END IF;
    SET ClusterStr = '';
    IF piIndexName IS NULL
    THEN
        IF piUnique = 2
        THEN
            SET @update_stmt = CONCAT('alter table ', piTableName, ' add primary key ', ClusterStr, ColNames);
        ELSEIF piUnique = 3
        THEN
            SET @update_stmt = CONCAT('alter table ', piTableName, ' add foreign key ', ColNames);
        ELSEIF piUnique = 4
        THEN
            SET @update_stmt = CONCAT('alter table ', piTableName, ' add check ', ColNames);
        ELSEIF piUnique = 1
        THEN
            SET @update_stmt = CONCAT('alter table ', piTableName, ' add unique ', ClusterStr, ColNames);
        END IF;
        PREPARE stmt FROM @update_stmt;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    ELSE
        IF NOT EXISTS(SELECT *
                      FROM information_schema.statistics
                      WHERE table_schema = (SELECT DATABASE())
                        AND table_name = piTableName
                        AND index_name = piIndexName)
        THEN
            SET @vCount = 1;
        ELSE
            SET @vCount = 0;
        END IF;
        IF @vCount = 1
        THEN
            IF piUnique = 1
            THEN
                SET @update_stmt = CONCAT('create unique ', ClusterStr, ' index ', piIndexName, ' on ', piTableName, '',
                                          ColNames);
            ELSEIF piUnique = 2
            THEN
                SET @update_stmt = CONCAT('alter table ', piTableName, ' add constraint ', piIndexName, ' primary key ',
                                          ClusterStr, ColNames);
            ELSEIF piUnique = 3
            THEN
                SET @update_stmt = CONCAT('alter table ', piTableName, ' add constraint ', piIndexName, ' foreign key ',
                                          ColNames);
            ELSEIF piUnique = 4
            THEN
                SET @update_stmt = CONCAT('alter table ', piTableName, ' add constraint ', piIndexName, ' check ',
                                          ColNames);
            ELSE
                SET @update_stmt = CONCAT('create ', ClusterStr, ' index ', piIndexName, ' on ', piTableName, '', ColNames);
            END IF;
        ELSE
            SELECT (CONCAT('WARNING: 增加索引', piTableName, '.', piIndexName, '失败，索引已存在'));
            SET @update_stmt = '0';
        END IF;
        IF @update_stmt <> '0'
        THEN
            PREPARE stmt FROM @update_stmt;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;
    END IF;
END;
$
-- /p chenrizhang 2019-10-25 存储过程，删除索引
DROP PROCEDURE IF EXISTS DROPINDEX;
$
CREATE PROCEDURE DROPINDEX(IN piTableName VARCHAR(200), IN piIndexName VARCHAR(200))
BEGIN
    IF EXISTS(SELECT *
              FROM information_schema.statistics
              WHERE table_schema = (SELECT DATABASE())
                AND table_name = piTableName
                AND index_name = piIndexName)
    THEN
        SET @update_stmt = CONCAT('drop index ', piIndexName, ' on ', piTableName);
        PREPARE stmt FROM @update_stmt;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END;
$

CREATE TABLE IF NOT EXISTS SR_SKU (
    uuid        varchar(38)  NOT NULL,
    spuid       varchar(100) NOT NULL,
    id          varchar(80)  NOT NULL,
    name        varchar(128) NOT NULL,
    image       varchar(512),
    images      text,
    remark      text,
    marketPrice decimal(19, 2),
    price       decimal(19, 2),
    stockQty    decimal(19, 3),
    tags        text,
    PRIMARY KEY (uuid)
) ENGINE = InnoDB;

CALL CREATEINDEX('IDX_SKU_1', 'SR_SKU', '(id)', 'index');

CREATE TABLE IF NOT EXISTS SR_SKU_DESCRIPTION (
    uuid    varchar(38)  NOT NULL,
    skuUuid varchar(38)  NOT NULL,
    format  varchar(128) NOT NULL,
    content text         NOT NULL,
    PRIMARY KEY (uuid)
) ENGINE = InnoDB;

CALL CREATEINDEX('IDX_SKU_DESCRIPTION_1', 'SR_SKU_DESCRIPTION', '(skuUuid)', 'index');

CREATE TABLE IF NOT EXISTS SR_SKU_PROPERTY (
    uuid    varchar(38)  NOT NULL,
    skuUuid varchar(38)  NOT NULL,
    name    varchar(128) NOT NULL,
    value   varchar(512) NOT NULL,
    PRIMARY KEY (uuid)
) ENGINE = InnoDB;

CALL CREATEINDEX('IDX_SKU_PROPERTY_1', 'SR_SKU_PROPERTY', '(skuUuid)', 'index');
