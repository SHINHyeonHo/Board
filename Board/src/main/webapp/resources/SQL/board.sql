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

-----------------------------------------------------------------------------------------------------------------------------
-- 07.14 --
show user;
-- USER이(가) "MYORAUSER"입니다.

create table board_img_advertise
(imgno          number not null
,imgfilename    varchar2(100) not null
,constraint PK_board_img_advertise primary key(imgno)
);

create sequence seq_img_advertise
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

insert into board_img_advertise values(seq_img_advertise.nextval, '미샤.png');
insert into board_img_advertise values(seq_img_advertise.nextval, '원더플레이스.png');
insert into board_img_advertise values(seq_img_advertise.nextval, '레노보.png');
insert into board_img_advertise values(seq_img_advertise.nextval, '동원.png');
commit;

select *
from board_img_advertise
order by imgno desc;

select * from tab;

select *
from mymvc_shopping_member
where userid='Shine';

---- 로그인 되어지는 회원에게 등급레벨을 부여하여 접근권한을 다르게 설정하도록 함. ----
alter table mymvc_shopping_member 
add gradelevel number(2) default 1;

update mymvc_shopping_member set gradelevel = 10
where userid in('leess', 'parkby');

commit;

select idx, userid, name, email, gradelevel
     , trunc( months_between(sysdate, lastPwdChangeDate) ) AS pwdchangegap  
	 , trunc( months_between(sysdate, lastLoginDate) ) AS lastlogindategap 
from mymvc_shopping_member 
where status = 1 and userid= 'Shine' and pwd = '9695b88a59a1610320897fa84cb7e144cc51f2984520efb77111d94b402a8382';


------------------------------------------------------------------------------------------------------------------------------------------------------



