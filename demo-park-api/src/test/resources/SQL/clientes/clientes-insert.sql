insert into USUARIOS (id, username, password, role) values (100, 'ana@email.com', '$2a$12$5ZPyWjSGIiKICZK8pq1Dluo.XgwmkGI/09zjz05hgauevmpABNPAa', 'ROLE_ADMIN');
insert into USUARIOS (id, username, password, role) values (101, 'bia@email.com', '$2a$12$5ZPyWjSGIiKICZK8pq1Dluo.XgwmkGI/09zjz05hgauevmpABNPAa', 'ROLE_CLIENTE');
insert into USUARIOS (id, username, password, role) values (102, 'bob@email.com', '$2a$12$5ZPyWjSGIiKICZK8pq1Dluo.XgwmkGI/09zjz05hgauevmpABNPAa', 'ROLE_CLIENTE');
insert into USUARIOS (id, username, password, role) values (103, 'toby@email.com', '$2a$12$5ZPyWjSGIiKICZK8pq1Dluo.XgwmkGI/09zjz05hgauevmpABNPAa', 'ROLE_CLIENTE');

insert into CLIENTES (id, nome, cpf, id_usuario) values (10, 'Bianca Silva', '03656968039', 101);
insert into CLIENTES (id, nome, cpf, id_usuario) values (20, 'Roberto Gomes', '39998186030', 102);