-- Seed data for demo scenarios

INSERT INTO zone_matrix VALUES ('381', '752', 4);  -- Memphis -> Dallas
INSERT INTO zone_matrix VALUES ('381', '100', 5);  -- Memphis -> NYC
INSERT INTO zone_matrix VALUES ('381', '902', 6);  -- Memphis -> LA
INSERT INTO zone_matrix VALUES ('381', '000', 8);  -- Memphis -> international placeholder

INSERT INTO service_base_rate VALUES (4, 'GND', 18.50);
INSERT INTO service_base_rate VALUES (4, 'EXP', 42.00);
INSERT INTO service_base_rate VALUES (4, 'PO',  58.00);
INSERT INTO service_base_rate VALUES (5, 'GND', 22.00);
INSERT INTO service_base_rate VALUES (5, 'EXP', 48.00);
INSERT INTO service_base_rate VALUES (5, 'PO',  65.00);
INSERT INTO service_base_rate VALUES (6, 'GND', 26.00);
INSERT INTO service_base_rate VALUES (6, 'EXP', 55.00);
INSERT INTO service_base_rate VALUES (6, 'PO',  72.00);
INSERT INTO service_base_rate VALUES (8, 'EXP', 95.00);

INSERT INTO account_tier VALUES ('ACCT-1001', 'STANDARD');
INSERT INTO account_tier VALUES ('ACCT-2002', 'SILVER');
INSERT INTO account_tier VALUES ('ACCT-3003', 'GOLD');

COMMIT;
