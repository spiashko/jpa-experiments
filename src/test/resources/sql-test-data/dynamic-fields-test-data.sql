delete
from person_field;

delete
from person;

insert into person
values ('person1'),
       ('person2'),
       ('person0')
;

insert into person_field
values ('1-fn', 'person1', 'firstname', 'fn person 1'),
       ('1-ln', 'person1', 'lastname', 'ln person 1'),
       ('2-fn', 'person2', 'firstname', 'fn person 2'),
       ('2-ln', 'person2', 'lastname', 'ln person 2'),
       ('0-fn', 'person0', 'firstname', 'fn person 0'),
       ('0-ln', 'person0', 'lastname', 'ln person 0')
;
