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
where userid in('admin', 'Shine');

commit;

select idx, userid, name, email, gradelevel
     , trunc( months_between(sysdate, lastPwdChangeDate) ) AS pwdchangegap  
	 , trunc( months_between(sysdate, lastLoginDate) ) AS lastlogindategap 
from mymvc_shopping_member 
where status = 1 and userid= 'Shine' and pwd = '9695b88a59a1610320897fa84cb7e144cc51f2984520efb77111d94b402a8382';



------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- 07. 15
    ------- **** 게시판(답변글쓰기가 없고, 파일첨부도 없는) 글쓰기 **** -------
desc mymvc_shopping_member;

create table tblBoard
(seq         number                not null    -- 글번호
,fk_userid   varchar2(20)          not null    -- 사용자ID
,name        varchar2(20)          not null    -- 글쓴이 
,subject     Nvarchar2(200)        not null    -- 글제목
,content     Nvarchar2(2000)       not null    -- 글내용   -- clob (최대 4GB까지 허용) 
,pw          varchar2(20)          not null    -- 글암호
,readCount   number default 0      not null    -- 글조회수
,regDate     date default sysdate  not null    -- 글쓴시간
,status      number(1) default 1   not null    -- 글삭제여부   1:사용가능한 글,  0:삭제된글
,constraint PK_tblBoard_seq primary key(seq)
,constraint FK_tblBoard_fk_userid foreign key(fk_userid) references mymvc_shopping_member(userid)
,constraint CK_tblBoard_status check( status in(0,1) )
);

create sequence boardSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

select *
from tblBoard
order by seq desc;

------------------------------------------------   07 . 16   -------------------------------------------------------------------
---- *** 게시판에서 이전글보기, 다음글보기 를 작성하고자 할때 사용하는 것이다. *** ---
           ---- *** lag() , lead() *** ---
  
  -- lag  ==> 어떤행의 바로앞의 몇번째 행을 가리키는 것.
  -- lead ==> 어떤행의 바로뒤의 몇번째 행을 가리키는 것.
  
  select lag(first_name || ' ' || last_name) over(order by salary desc)  
       , lag(salary) over(order by salary desc) 
       
       , employee_id
       , first_name || ' ' || last_name AS ENAME
       , salary
       
       , lead(first_name || ' ' || last_name) over(order by salary desc) 
       , lead(salary) over(order by salary desc) 
  from employees;


