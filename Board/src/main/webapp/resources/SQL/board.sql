--- **** 스프링 게시판 **** ----

show user;
-- USER이(가) "MYORAUSER"입니다.

desc employees;

select employee_id
     , first_name || ' ' || last_name AS ename
     , nvl(salary*12 + salary*commission_pct, salary*12) AS yearpay
     , case when substr(jubun, 7, 1) in('1', '3') then '남자' else '여' end AS gender
     , extract(year from sysdate) - ( case when substr(jubun, 7,1) in('1', '2') then 1900 else 2000 end + to_number(substr(jubun, 1, 2)) ) AS age
from employees;

http://localhost:9090/test/employees.action
-- datatables 페이징 처리
-- .tiles1 로

create table spring_test1
(no     number
,name   varchar2(100)
,writeday   date default sysdate
);

select *
from spring_test1;

insert into spring_test1(no, name, writeday)
values(23, '신현', default);

commit;

delete from spring_test1;
commit;

