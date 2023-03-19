--
-- Custom data for Integration/Functional Tests, ignores default data.sql (See application-ittest.properties). --
--

-- global authorities
INSERT INTO AUTHORITIES (ID, AUTHORITY_NAME) VALUES (1, 'ADMIN');
INSERT INTO AUTHORITIES (ID, AUTHORITY_NAME) VALUES (2, 'VIEWER');

-- user roles (not yet used)
INSERT INTO USER_ROLES (ID,ROLE_NAME)
VALUES (1,'DEFAULT_ROLE_ADMIN'),
       (2,'DEFAULT_ROLE_VIEWER');

INSERT INTO `user_role_authorities` (user_role_id,authority_id)
VALUES (1,1),
       (1,2),
       (2,2);

-- domain authorities
INSERT INTO AUTHORITIES (ID, AUTHORITY_NAME) VALUES (10, 'KVAARDER_ADMIN');
INSERT INTO AUTHORITIES (ID, AUTHORITY_NAME) VALUES (20, 'KVAARDER_EDITOR');
INSERT INTO AUTHORITIES (ID, AUTHORITY_NAME) VALUES (30, 'KVAARDER_VIEWER');

-- Add/merge default ldap roles (conditional):
INSERT INTO `ldap_roles`
       VALUES (101, 'cn=g-domainx-admin,ou=groups,dc=mydomain,dc=com', 'LDAP_MEMBEROF');
INSERT INTO `ldap_roles`
       VALUES (666, 'cn=Kaapvaarders,ou=groups,dc=mydomain,dc=com', 'LDAP_MEMBEROF');

-- Add Authorities to ldap Roles: Beware of AuthorityName enums
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 101, ID FROM authorities WHERE AUTHORITY_NAME = 'ADMIN';
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 101, ID FROM authorities WHERE AUTHORITY_NAME = 'VIEWER';
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 666, ID FROM authorities WHERE AUTHORITY_NAME = 'KVAARDER_ADMIN';
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 666, ID FROM authorities WHERE AUTHORITY_NAME = 'KVAARDER_EDITOR';
