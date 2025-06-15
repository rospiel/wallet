insert into `system_parameter`(`data`, `description`, `name`, `version`, `created_by`, `created_at`)
values('0', 'Last number account used', 'LAST_NUMBER_ACCOUNT', 0, 'System', CURRENT_TIMESTAMP());

insert into `type_wallet_movements`(`description`, `name`, `created_by`, `created_at`)
values('Deposit', 'DEPOSIT', 'System', CURRENT_TIMESTAMP()),
      ('Withdrawal', 'WITHDRAWAL', 'System', CURRENT_TIMESTAMP()),
      ('Transfer Between Accounts', 'TRANSFER_BETWEEN_ACCOUNTS', 'System', CURRENT_TIMESTAMP());