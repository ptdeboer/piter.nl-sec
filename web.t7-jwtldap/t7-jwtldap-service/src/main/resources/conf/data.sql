--
-- Default data for demo purposes
--

-- local authorities, only used by the default (local) admin account.
INSERT INTO AUTHORITIES (ID, AUTHORITY_NAME) VALUES (1, 'LOCAL_ADMIN');
INSERT INTO AUTHORITIES (ID, AUTHORITY_NAME) VALUES (2, 'LOCAL_VIEWER');

-- local user roles (not yet used in demo)
INSERT INTO USER_ROLES (ID,ROLE_NAME)
       VALUES (1,'LOCAL_ROLE_ADMIN'),
              (2,'LOCAL_ROLE_VIEWER');

INSERT INTO `user_role_authorities` (user_role_id,authority_id)
       VALUES (1,1),
              (1,2),
              (2,2);

-- domain roles
INSERT INTO AUTHORITIES (ID, AUTHORITY_NAME) VALUES (10, 'KAAPVAARDERS_ADMIN');
INSERT INTO AUTHORITIES (ID, AUTHORITY_NAME) VALUES (20, 'KAAPVAARDERS_EDITOR');
INSERT INTO AUTHORITIES (ID, AUTHORITY_NAME) VALUES (30, 'KAAPVAARDERS_VIEWER');

-- ldap membership roles
-- Add/merge default ldap roles (conditional):
INSERT INTO `ldap_roles`
       VALUES (100, 'cn=local-admin-group,ou=groups,dc=ldap-dev,dc=local', 'LDAP_MEMBEROF'),
              (101, 'cn=g-domain-kaapvaarders-admin,ou=groups,dc=kaapvaarders,dc=com', 'LDAP_MEMBEROF'),
              (102, 'cn=g-domain-kaapvaarders-viewers,ou=groups,dc=kaapvaarders,dc=com', 'LDAP_MEMBEROF');

-- Add authorities to ldap roles:
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 100, ID FROM authorities WHERE AUTHORITY_NAME = 'LOCAL_ADMIN';
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 100, ID FROM authorities WHERE AUTHORITY_NAME = 'LOCAL_EDITOR';
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 100, ID FROM authorities WHERE AUTHORITY_NAME = 'LOCAL_VIEWER';
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 101, ID FROM authorities WHERE AUTHORITY_NAME = 'KAAPVAARDERS_ADMIN';
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 101, ID FROM authorities WHERE AUTHORITY_NAME = 'KAAPVAARDERS_EDITOR';
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 101, ID FROM authorities WHERE AUTHORITY_NAME = 'KAAPVAARDERS_VIEWER';
INSERT INTO ldap_role_authorities (LDAP_ROLE_ID, AUTHORITY_ID) SELECT 102, ID FROM authorities WHERE AUTHORITY_NAME = 'KAAPVAARDERS_VIEWER';
