-- Test exceptionTrace: OmsBuilderToSql.orderDtoBuilder
-- Test value: [1]
WITH l AS (WITH ol AS (SELECT  ot.orderId AS orderId, ot.log AS log  FROM orderLog ot GROUP BY ot.orderId,ot.log) SELECT  ol.orderId AS orderId, ol.log AS log  FROM ol WHERE ol.orderId > ?  GROUP BY ol.orderId,ol.log) SELECT DISTINCT o.id AS c0  FROM `order` o INNER JOIN orderItem i ON o.id = i.orderId ANY LEFT JOIN  l ON o.id = l.orderId WHERE i.name IN ( 'test' ) AND i.name IS NOT NULL  AND l.log IS NOT NULL  GROUP BY o.id ORDER BY o.id DESC;
-- -------------------------------------------

-- Test exceptionTrace: OmsBuilderToSql.testSubBuilder
-- Test value: []
SELECT  c.id AS id, c.type AS type  FROM (SELECT  id, cat_type AS type  FROM cat) c;
-- -------------------------------------------

