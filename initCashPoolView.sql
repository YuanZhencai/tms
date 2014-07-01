---------公司------------
drop view vw_stat_company;
create view vw_stat_company(procType,cpId,cpName,procId,amount,payId,zf,sjxb,payDate,terminateFlag) as(
--采购资金（贸易）借款/转款流程
SELECT 'zjmy',cp.ID,cp.COMPANY_NAME,zjmy.ID,zjmy.AMOUNT,pay.ID,pay.PAY_FUNDS_TOTAL,CASE WHEN (pay.PAY_WAY = 'O' and zjmy.TERMINATE_FLAG='N') THEN pay.PAY_FUNDS_TOTAL WHEN (status.TMS_STATUS='4' and zjmy.TERMINATE_FLAG='N') THEN pay.PAY_FUNDS_TOTAL ELSE 0 END,zjmy.PAYMENT_DATE,zjmy.TERMINATE_FLAG
        FROM COMPANY cp  join PROC_PURCHASE_FUND_TRADE zjmy on(zjmy.COMPANY_ID=cp.ID)
        left join PROC_PURCHASE_FUND_TRADE_PAY pay on(pay.PROC_INST_ID=zjmy.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=pay.ID)
         
union all
--采购资金（生产）借款/转款流程        
SELECT 'zjsc',cp.ID,cp.COMPANY_NAME,zjsc.ID,zjsc.AMOUNT,pay.ID,pay.PAY_FUNDS_TOTAL,CASE WHEN (pay.PAY_WAY = 'O' and zjsc.TERMINATE_FLAG='N') THEN pay.PAY_FUNDS_TOTAL WHEN (status.TMS_STATUS='4' and zjsc.TERMINATE_FLAG='N') THEN pay.PAY_FUNDS_TOTAL ELSE 0 END,zjsc.PAYMENT_DATE,zjsc.TERMINATE_FLAG
        FROM COMPANY cp  join PROC_PURCHASE_FUND_PROD zjsc on(zjsc.COMPANY_ID=cp.ID)
        left join PROC_PURCHASE_FUND_PROD_PAY pay on(pay.PROC_INST_ID=zjsc.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=pay.ID)
        
union all
--投资、融资保证金及归还银行贷款借款/转款
SELECT 'tzrz',cp.ID,cp.COMPANY_NAME,tzrz.ID,tzrz.AMOUNT,tzrzd.ID,tzrzd.PAY_FUNDS_TOTAL,CASE WHEN (tzrzd.PAY_WAY = 'O' and tzrz.TERMINATE_FLAG='N') THEN tzrzd.PAY_FUNDS_TOTAL WHEN (status.TMS_STATUS='4' and tzrz.TERMINATE_FLAG='N') THEN tzrzd.PAY_FUNDS_TOTAL ELSE 0 END,tzrz.PAYMENT_DATE,tzrz.TERMINATE_FLAG
        FROM COMPANY cp join PROC_INVE_FINA_BAIL tzrz on(tzrz.COMPANY_ID=cp.ID)
        left join PROC_INVE_FINA_DETAIL tzrzd on(tzrzd.PROC_INST_ID=tzrz.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=tzrzd.ID)
        
union all
--日常付款借款/转款流程
SELECT 'rcfk',cp.ID,cp.COMPANY_NAME,rcfk.ID,rcfk.AMOUNT,rcfkd.id,rcfkd.PAY_FUNDS_TOTAL,CASE WHEN (rcfkd.PAY_WAY = 'O' and rcfk.TERMINATE_FLAG='N') THEN rcfkd.PAY_FUNDS_TOTAL WHEN (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') THEN rcfkd.PAY_FUNDS_TOTAL ELSE 0 END,rcfk.PAYMENT_DATE,rcfk.TERMINATE_FLAG
        FROM COMPANY cp join PROC_DAILY_PAY_LOAN_TRAN  rcfk on(cp.ID=rcfk.COMPANY_ID)
        left join PROC_DAILY_PAY_DETAIL rcfkd on(rcfkd.PROC_INST_ID=rcfk.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=rcfkd.ID)
        
union all
--付工程款股利及归还股东借款流程
SELECT 'gck',cp.ID,cp.COMPANY_NAME,gck.ID,gck.AMOUNT,gckd.id,gckd.PAY_FUNDS_TOTAL,CASE WHEN (gckd.PAY_WAY = 'O' and gck.TERMINATE_FLAG='N') THEN gckd.PAY_FUNDS_TOTAL WHEN (status.TMS_STATUS='4' and gck.TERMINATE_FLAG='N') THEN gckd.PAY_FUNDS_TOTAL ELSE 0 END,gck.PAYMENT_DATE,gck.TERMINATE_FLAG   
        FROM COMPANY cp join PROC_PAY_PROJECT  gck on(gck.COMPANY_ID=cp.ID)
        left join PROC_PAY_PROJECT_DETAILS gckd on(gckd.PROC_INST_ID=gck.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=gckd.ID)
);

-----可用余额------
drop view vw_stat_cpAvAmount;
create view vw_stat_cpAvAmount(cpId,pcName,account,zfId,avTime,avAmount) as(
select cp.ID,cp.COMPANY_NAME,cpAb.ACCOUNT,cpAb.ID,cpAb.TRANS_DATETIME,cpAb.AVAILABLE_AMOUNT from COMPANY cp join COMPANY_ACCOUNT cpA on(cpA.COMPANY_ID=cp.Id)
        join COMPANY_ACCOUNT_BALANCE cpAb on(cpAb.ACCOUNT=cpA.ACCOUNT) 
);


------品项----------
drop view vw_stat_px;
create view  vw_stat_px(procType,cpId,pxKey,pxName,amountId,amount,payId,sjxb,payDate,terminateFlag) as(
--采购资金（贸易）借款/转款流程
SELECT 'zjmy',zjmy.COMPANY_ID,dc.CODE_KEY,dc.CODE_VAL,px.ID,px.VARIETY_AMOUNT,payd.ID,CASE WHEN (pay.PAY_WAY='O' and zjmy.TERMINATE_FLAG='N') THEN payd.VARIETY_AMOUNT WHEN (status.TMS_STATUS='4' and zjmy.TERMINATE_FLAG='N') THEN payd.VARIETY_AMOUNT ELSE 0 END,zjmy.PAYMENT_DATE,zjmy.TERMINATE_FLAG 
        from PROC_PURCHASE_FUND_TRADE_PRODUCT px join DICT dc on(dc.CODE_KEY=px.VARIETY and (dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.FOOD' or dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.OIL') ) 
        left join PROC_PURCHASE_FUND_TRADE zjmy on(zjmy.ID=px.PROC_INST_ID) 
        left join PROC_PURCHASE_FUND_TRADE_PAY pay on(pay.PROC_INST_ID=zjmy.ID)
        left join PROC_PURCHASE_FUND_TRADE_PAY_DETAIL payd on(payd.PAY_ID = pay.ID and payd.PRODUCT_ID=px.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=pay.ID)
        
union all
--采购资金（生产）借款/转款流程        
SELECT 'zjsc',zjsc.COMPANY_ID,dc.CODE_KEY,dc.CODE_VAL,px.ID,px.VARIETY_AMOUNT,payd.ID,CASE WHEN (pay.PAY_WAY='O' and zjsc.TERMINATE_FLAG='N') THEN payd.VARIETY_AMOUNT WHEN (status.TMS_STATUS='4' and zjsc.TERMINATE_FLAG='N') THEN payd.VARIETY_AMOUNT ELSE 0 END,zjsc.PAYMENT_DATE,zjsc.TERMINATE_FLAG  
        from PROC_PURCHASE_FUND_PROD_PRODUCT px join DICT dc on(dc.CODE_KEY=px.VARIETY and (dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.FOOD' or dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.OIL') ) 
        left join PROC_PURCHASE_FUND_PROD zjsc on(zjsc.ID=px.PROC_INST_ID) left join PROC_PURCHASE_FUND_PROD_PAY pay on(pay.PROC_INST_ID=zjsc.ID)
        left join PROC_PURCHASE_FUND_PROD_PAY_DETAIL payd on(payd.PAY_ID = pay.ID and payd.PRODUCT_ID=px.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=pay.ID)
union all
--投资、融资保证金及归还银行贷款借款/转款
--表单类型'I'--投资理财（纯理财）,'F'--融资保证金,'R'--还贷,'C'--利息及融资费用
SELECT 'tzrz',tzrz.COMPANY_ID,tzrz.FORM_TYPE,
        case when tzrz.FORM_TYPE='I' then '投资理财（纯理财）' else (
        case when tzrz.FORM_TYPE='F' then '融资保证金' else(
        case when tzrz.FORM_TYPE='R' then '还贷' else(
        case when tzrz.FORM_TYPE='C' then '利息及融资费用' else(
        case when tzrz.FORM_TYPE='D' then '时点存款' else '' end
        )end
        ) end
        ) end
        ) end,
        tzrz.ID,tzrz.AMOUNT,tzrzd.ID,CASE WHEN (tzrzd.PAY_WAY='O' and tzrz.TERMINATE_FLAG='N') THEN tzrzd.PAY_FUNDS_TOTAL WHEN (status.TMS_STATUS='4' and tzrz.TERMINATE_FLAG='N') THEN tzrzd.PAY_FUNDS_TOTAL ELSE 0 END,tzrz.PAYMENT_DATE,tzrz.TERMINATE_FLAG 
        FROM PROC_INVE_FINA_BAIL tzrz  left join PROC_INVE_FINA_DETAIL tzrzd on (tzrzd.PROC_INST_ID=tzrz.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=tzrzd.ID)
union all 
--日常付款借款/转款流程
        --税务
        SELECT 'rcfk',rcfk.COMPANY_ID,'TAX','税款',rcfk.ID,rcfk.TAX,rcfkd.ID,
        		CASE WHEN (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.TAX else cast((rcfk.TAX*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        		WHEN (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.TAX else cast((rcfk.TAX*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) ELSE 0 END
                ,rcfk.PAYMENT_DATE,rcfk.TERMINATE_FLAG  FROM PROC_DAILY_PAY_LOAN_TRAN rcfk left join PROC_DAILY_PAY_DETAIL rcfkd on(rcfkd.PROC_INST_ID=rcfk.ID)
               left join PROC_TMS_STATUS status on(status.PAY_ID=rcfkd.ID) where rcfk.TAX is not null and rcfk.TAX > 0
        union all
        --辅料包料
        SELECT 'rcfk',rcfk.COMPANY_ID,'PACKAGING_MATERIALS','辅料/包材/备件',rcfk.ID,rcfk.PACKAGING_MATERIALS,rcfkd.ID,
        		CASE WHEN (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.PACKAGING_MATERIALS else cast((rcfk.PACKAGING_MATERIALS*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        		WHEN (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.PACKAGING_MATERIALS else cast((rcfk.PACKAGING_MATERIALS*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) ELSE 0 END
                ,rcfk.PAYMENT_DATE,rcfk.TERMINATE_FLAG FROM PROC_DAILY_PAY_LOAN_TRAN rcfk left join PROC_DAILY_PAY_DETAIL rcfkd on(rcfkd.PROC_INST_ID=rcfk.ID)
               left join PROC_TMS_STATUS status on(status.PAY_ID=rcfkd.ID) where rcfk.PACKAGING_MATERIALS is not null and rcfk.PACKAGING_MATERIALS > 0
        union all
       --水电气
       SELECT 'rcfk',rcfk.COMPANY_ID,'STEAM_ELECTRICITY','水/电/汽/煤',rcfk.ID,rcfk.STEAM_ELECTRICITY,rcfkd.ID,
       			CASE WHEN (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.STEAM_ELECTRICITY else cast((rcfk.STEAM_ELECTRICITY*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
       			WHEN (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.STEAM_ELECTRICITY else cast((rcfk.STEAM_ELECTRICITY*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) ELSE 0 END
                ,rcfk.PAYMENT_DATE,rcfk.TERMINATE_FLAG FROM PROC_DAILY_PAY_LOAN_TRAN rcfk left join PROC_DAILY_PAY_DETAIL rcfkd on(rcfkd.PROC_INST_ID=rcfk.ID)
               left join PROC_TMS_STATUS status on(status.PAY_ID=rcfkd.ID)  where rcfk.STEAM_ELECTRICITY is not null and rcfk.STEAM_ELECTRICITY > 0
       union all
       --工资
       SELECT 'rcfk',rcfk.COMPANY_ID,'SALARY','工资/社保/花红',rcfk.ID,rcfk.SALARY,rcfkd.ID,
       			CASE WHEN (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.SALARY else cast((rcfk.SALARY*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
       			WHEN (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.SALARY else cast((rcfk.SALARY*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) ELSE 0 END
                ,rcfk.PAYMENT_DATE,rcfk.TERMINATE_FLAG FROM PROC_DAILY_PAY_LOAN_TRAN rcfk left join PROC_DAILY_PAY_DETAIL rcfkd on(rcfkd.PROC_INST_ID=rcfk.ID)
               left join PROC_TMS_STATUS status on(status.PAY_ID=rcfkd.ID) where rcfk.SALARY is not null and rcfk.SALARY > 0
       union all
       --运费港杂费
       SELECT 'rcfk',rcfk.COMPANY_ID,'FREIGHT','运费/港杂费/加工费',rcfk.ID,rcfk.FREIGHT,rcfkd.ID,
       			CASE WHEN (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.FREIGHT else cast((rcfk.FREIGHT*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
       			WHEN (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.FREIGHT else cast((rcfk.FREIGHT*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) ELSE 0 END
                ,rcfk.PAYMENT_DATE,rcfk.TERMINATE_FLAG FROM PROC_DAILY_PAY_LOAN_TRAN rcfk left join PROC_DAILY_PAY_DETAIL rcfkd on(rcfkd.PROC_INST_ID=rcfk.ID)
               left join PROC_TMS_STATUS status on(status.PAY_ID=rcfkd.ID) where rcfk.FREIGHT is not null and rcfk.FREIGHT > 0
       union all
       --其他
       SELECT 'rcfk',rcfk.COMPANY_ID,'ELSE_PROJ','其他',rcfk.ID,rcfk.ELSE_PROJ,rcfkd.ID,
       			CASE WHEN (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.ELSE_PROJ else cast((rcfk.ELSE_PROJ*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
       			WHEN (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') THEN (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.ELSE_PROJ else cast((rcfk.ELSE_PROJ*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) ELSE 0 END
                ,rcfk.PAYMENT_DATE,rcfk.TERMINATE_FLAG FROM PROC_DAILY_PAY_LOAN_TRAN rcfk left join PROC_DAILY_PAY_DETAIL rcfkd on(rcfkd.PROC_INST_ID=rcfk.ID)
               left join PROC_TMS_STATUS status on(status.PAY_ID=rcfkd.ID) where rcfk.ELSE_PROJ is not null and rcfk.ELSE_PROJ > 0
union all
--付工程款股利及归还股东借款流程
SELECT 'gck',gck.COMPANY_ID,gck.FORM_TYPE,
        case when gck.FORM_TYPE='E' then '付工程款' else (
        case when gck.FORM_TYPE='D' then '支付股利' else(
        case when gck.FORM_TYPE='A' then '归还股东借款' else '' end
        ) end
        ) end,
        gck.ID,gck.AMOUNT,gckd.ID,CASE WHEN (gckd.PAY_WAY='O' and gck.TERMINATE_FLAG='N') THEN gckd.PAY_FUNDS_TOTAL WHEN (status.TMS_STATUS='4' and gck.TERMINATE_FLAG='N') THEN gckd.PAY_FUNDS_TOTAL ELSE 0 END,gck.PAYMENT_DATE,gck.TERMINATE_FLAG 
        FROM PROC_PAY_PROJECT gck left join PROC_PAY_PROJECT_DETAILS gckd on(gckd.PROC_INST_ID=gck.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=gckd.ID)
);


---------公司+品项------------
drop view vw_stat_companypx;
create view vw_stat_companypx(ptype,procId,companyId,payDate,tzlc,lxyh,sdck,rzzk,rzfzk,hdzk,hdfzk,gck,gl,ghgd,zjgl,zjfgl,rcfk) as(
--投资、融资保证金及归还银行贷款借款/转款
--表单类型'I'--投资理财（纯理财）,'F'--融资保证金,'R'--还贷,'C'--利息及融资费用
SELECT 'tzrz',tzrz.ID,tzrz.COMPANY_ID,tzrz.PAYMENT_DATE,
        sum(case when (tzrzd.PAY_WAY='O' and tzrz.FORM_TYPE='I' and tzrz.TERMINATE_FLAG='N') then tzrzd.PAY_FUNDS_TOTAL when (status.TMS_STATUS='4' and tzrz.FORM_TYPE='I') then tzrzd.PAY_FUNDS_TOTAL else 0 end) as tzlc,
        sum(case when (tzrzd.PAY_WAY='O' and tzrz.FORM_TYPE='C' and tzrz.TERMINATE_FLAG='N') then tzrzd.PAY_FUNDS_TOTAL when (status.TMS_STATUS='4' and tzrz.FORM_TYPE='C') then tzrzd.PAY_FUNDS_TOTAL else 0 end) as lxyh,
        sum(case when (tzrzd.PAY_WAY='O' and tzrz.FORM_TYPE='D' and tzrz.TERMINATE_FLAG='N') then tzrzd.PAY_FUNDS_TOTAL when (status.TMS_STATUS='4' and tzrz.FORM_TYPE='D') then tzrzd.PAY_FUNDS_TOTAL else 0 end) as sdck,
        sum(case when (tzrzd.PAY_WAY='O' and tzrz.FORM_TYPE='F' and tzrz.TRANSIT='Y' and tzrz.TERMINATE_FLAG='N') then tzrzd.PAY_FUNDS_TOTAL when (status.TMS_STATUS='4' and tzrz.FORM_TYPE='F' and tzrz.TRANSIT='Y') then tzrzd.PAY_FUNDS_TOTAL else 0 end) as rzzk,
        sum(case when (tzrzd.PAY_WAY='O' and tzrz.FORM_TYPE='F' and tzrz.TRANSIT='N' and tzrz.TERMINATE_FLAG='N') then tzrzd.PAY_FUNDS_TOTAL when (status.TMS_STATUS='4' and tzrz.FORM_TYPE='F' and tzrz.TRANSIT='N') then tzrzd.PAY_FUNDS_TOTAL else 0 end) as rzfzk,
        sum(case when (tzrzd.PAY_WAY='O' and tzrz.FORM_TYPE='R' and tzrz.TRANSIT='Y' and tzrz.TERMINATE_FLAG='N') then tzrzd.PAY_FUNDS_TOTAL when (status.TMS_STATUS='4' and tzrz.FORM_TYPE='R' and tzrz.TRANSIT='Y') then tzrzd.PAY_FUNDS_TOTAL else 0 end) as hdzk,
        sum(case when (tzrzd.PAY_WAY='O' and tzrz.FORM_TYPE='R' and tzrz.TRANSIT='N' and tzrz.TERMINATE_FLAG='N') then tzrzd.PAY_FUNDS_TOTAL when (status.TMS_STATUS='4' and tzrz.FORM_TYPE='R' and tzrz.TRANSIT='N') then tzrzd.PAY_FUNDS_TOTAL else 0 end) as hdfzk,
        0 as gck, 0 as gl, 0 as ghgd, 0 as zjgl,0 as zjfgl,0 as rcfk
        FROM  PROC_INVE_FINA_BAIL tzrz left join PROC_INVE_FINA_DETAIL tzrzd on(tzrzd.PROC_INST_ID=tzrz.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=tzrzd.ID)
        group by 'tzrz',tzrz.ID,tzrz.COMPANY_ID,tzrz.PAYMENT_DATE
union all
--付工程款股利及归还股东借款流程
SELECT 'gck',gck.ID,gck.COMPANY_ID,gck.PAYMENT_DATE,
        0 as tzlc, 0 as lxyh, 0 as sdck, 0 as rzzk, 0 as rzfzk, 0 as hdzk, 0 as hdfzk,
        sum(case when (gckd.PAY_WAY='O' and gck.FORM_TYPE='E' and gck.TERMINATE_FLAG='N') then gckd.PAY_FUNDS_TOTAL when (status.TMS_STATUS='4' and gck.FORM_TYPE='E') then gckd.PAY_FUNDS_TOTAL else 0 end) as fgck,
        sum(case when (gckd.PAY_WAY='O' and gck.FORM_TYPE='D' and gck.TERMINATE_FLAG='N') then gckd.PAY_FUNDS_TOTAL when (status.TMS_STATUS='4' and gck.FORM_TYPE='D') then gckd.PAY_FUNDS_TOTAL else 0 end) as gl,
        sum(case when (gckd.PAY_WAY='O' and gck.FORM_TYPE='A' and gck.TERMINATE_FLAG='N') then gckd.PAY_FUNDS_TOTAL when (status.TMS_STATUS='4' and gck.FORM_TYPE='A') then gckd.PAY_FUNDS_TOTAL else 0 end) as ghgd,
        0 as zjgl,0 as zjfgl,0 as rcfk
        FROM  PROC_PAY_PROJECT  gck left join PROC_PAY_PROJECT_DETAILS gckd on(gckd.PROC_INST_ID=gck.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=gckd.ID)
        group by  'gck',gck.ID,gck.COMPANY_ID,gck.PAYMENT_DATE
union all
--采购资金（贸易）借款/转款流程
SELECT 'zjmy',zjmy.ID,zjmy.COMPANY_ID,zjmy.PAYMENT_DATE,
        0 as tzlc, 0 as lxyh, 0 as sdck, 0 as rzzk, 0 as rzfzk, 0 as hdzk, 0 as hdfzk, 0 as gck, 0 as gl, 0 as ghgd,
        sum(case when (pay.PAY_WAY='O' and px.VARIETY_RELATED='Y' and zjmy.TERMINATE_FLAG='N') then payd.VARIETY_AMOUNT when (status.TMS_STATUS='4' and px.VARIETY_RELATED='Y') then payd.VARIETY_AMOUNT else 0 end)  as zjgl,
        sum(case when (pay.PAY_WAY='O' and px.VARIETY_RELATED='N' and zjmy.TERMINATE_FLAG='N') then payd.VARIETY_AMOUNT when (status.TMS_STATUS='4' and px.VARIETY_RELATED='N') then payd.VARIETY_AMOUNT else 0 end)  as zjfgl,
        0 as rcfk
        from 
         PROC_PURCHASE_FUND_TRADE zjmy left  join PROC_PURCHASE_FUND_TRADE_PRODUCT px  on(zjmy.ID=px.PROC_INST_ID)  
        left join PROC_PURCHASE_FUND_TRADE_PAY pay on(pay.PROC_INST_ID=zjmy.ID)
        left join PROC_PURCHASE_FUND_TRADE_PAY_DETAIL payd on(payd.PAY_ID = pay.ID and payd.PRODUCT_ID=px.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=pay.ID)
        group by  'zjmy',zjmy.ID,zjmy.COMPANY_ID,zjmy.PAYMENT_DATE
union all
--采购资金（生产）借款/转款流程  
SELECT 'zjsc',zjsc.ID,zjsc.COMPANY_ID,zjsc.PAYMENT_DATE,
        0 as tzlc, 0 as lxyh, 0 as sdck, 0 as rzzk, 0 as rzfzk, 0 as hdzk, 0 as hdfzk, 0 as gck, 0 as gl, 0 as ghgd,
        sum(case when (pay.PAY_WAY='O' and px.VARIETY_RELATED='Y' and zjsc.TERMINATE_FLAG='N') then payd.VARIETY_AMOUNT when (status.TMS_STATUS='4' and px.VARIETY_RELATED='Y') then payd.VARIETY_AMOUNT else 0 end) as zjgl,
        sum(case when (pay.PAY_WAY='O' and px.VARIETY_RELATED='N' and zjsc.TERMINATE_FLAG='N') then payd.VARIETY_AMOUNT when (status.TMS_STATUS='4' and px.VARIETY_RELATED='N') then payd.VARIETY_AMOUNT else 0 end) as zjfgl,
        0 as rcfk
        from 
        PROC_PURCHASE_FUND_PROD zjsc left join PROC_PURCHASE_FUND_PROD_PRODUCT px  on(zjsc.ID=px.PROC_INST_ID)  
        left join PROC_PURCHASE_FUND_PROD_PAY pay on(pay.PROC_INST_ID=zjsc.ID)
        left join PROC_PURCHASE_FUND_PROD_PAY_DETAIL payd on(payd.PAY_ID = pay.ID and payd.PRODUCT_ID=px.ID)left join PROC_TMS_STATUS status on(status.PAY_ID=pay.ID)  
        group by  'zjsc',zjsc.ID,zjsc.COMPANY_ID,zjsc.PAYMENT_DATE
union all
--日常付款借款/转款流程
SELECT 'rcfk',rcfk.ID,rcfk.COMPANY_ID,rcfk.PAYMENT_DATE,
        0 as tzlc, 0 as lxyh, 0 as sdck, 0 as rzzk, 0 as rzfzk, 0 as hdzk, 0 as hdfzk, 0 as gck, 0 as gl, 0 as ghgd,0 as zjgl,0 as zjfgl,
        sum(case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfkd.PAY_FUNDS_TOTAL-rcfk.TRANSFER-rcfk.MARKET_DEDICATED else rcfkd.PAY_FUNDS_TOTAL-cast((rcfk.TRANSFER*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4))-cast((rcfk.MARKET_DEDICATED*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfkd.PAY_FUNDS_TOTAL-rcfk.TRANSFER-rcfk.MARKET_DEDICATED else rcfkd.PAY_FUNDS_TOTAL-cast((rcfk.TRANSFER*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4))-cast((rcfk.MARKET_DEDICATED*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) else 0 end)
        as rcfk
        FROM COMPANY cp join PROC_DAILY_PAY_LOAN_TRAN  rcfk on(cp.ID=rcfk.COMPANY_ID)
        left join PROC_DAILY_PAY_DETAIL rcfkd on(rcfkd.PROC_INST_ID=rcfk.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=rcfkd.ID)
        group by 'rcfk',rcfk.ID,rcfk.COMPANY_ID,rcfk.PAYMENT_DATE
);

-------公司-贸易/生产的关联/非关联------
drop view vw_stat_zjmysc;
create view vw_stat_zjmysc(type,pxKey,pxName,related,porcId,cpId,sjxb,payDate) as(
SELECT 'zjmy',dc.CODE_KEY,dc.CODE_VAL,px.VARIETY_RELATED,payd.ID,zjmy.COMPANY_ID,
        sum(case when (pay.PAY_WAY='O' and zjmy.TERMINATE_FLAG='N') then payd.VARIETY_AMOUNT when (status.TMS_STATUS='4' and zjmy.TERMINATE_FLAG='N') then payd.VARIETY_AMOUNT else 0 end),zjmy.PAYMENT_DATE
        from PROC_PURCHASE_FUND_TRADE_PRODUCT px join DICT dc on(dc.CODE_KEY=px.VARIETY and (dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.FOOD' or dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.OIL') ) 
        left join PROC_PURCHASE_FUND_TRADE zjmy on(zjmy.ID=px.PROC_INST_ID) left join PROC_PURCHASE_FUND_TRADE_PAY pay on(pay.PROC_INST_ID=zjmy.ID)
        left join PROC_PURCHASE_FUND_TRADE_PAY_DETAIL payd on(payd.PAY_ID = pay.ID and payd.PRODUCT_ID=px.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=pay.ID) 
        group by 'zjmy','zjmy',dc.CODE_KEY,dc.CODE_VAL,px.VARIETY_RELATED,payd.ID,zjmy.COMPANY_ID,zjmy.PAYMENT_DATE
union all
--采购资金（生产）借款/转款流程        
SELECT 'zjsc',dc.CODE_KEY ,dc.CODE_VAL,px.VARIETY_RELATED,payd.ID,zjsc.COMPANY_ID,
        sum(case when (pay.PAY_WAY='O' and zjsc.TERMINATE_FLAG='N') then payd.VARIETY_AMOUNT when (status.TMS_STATUS='4' and zjsc.TERMINATE_FLAG='N') then payd.VARIETY_AMOUNT else 0 end),zjsc.PAYMENT_DATE 
        from PROC_PURCHASE_FUND_PROD_PRODUCT px join DICT dc on(dc.CODE_KEY=px.VARIETY and (dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.FOOD' or dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.OIL') ) 
        left join PROC_PURCHASE_FUND_PROD zjsc on(zjsc.ID=px.PROC_INST_ID) left join PROC_PURCHASE_FUND_PROD_PAY pay on(pay.PROC_INST_ID=zjsc.ID)
        left join PROC_PURCHASE_FUND_PROD_PAY_DETAIL payd on(payd.PAY_ID = pay.ID and payd.PRODUCT_ID=px.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=pay.ID)
        group by 'zjsc',dc.CODE_KEY ,dc.CODE_VAL,px.VARIETY_RELATED,payd.ID,zjsc.COMPANY_ID,zjsc.PAYMENT_DATE
);

---日常付款：明细----
drop view vw_stat_rcfk;
create view vw_stat_rcfk(procId,cpId,cpName,total,tax,packMater,sperPart,coal,steam,salary,freight,elseProj,marketDedicted,transfer,payDate) as(
SELECT rcfk.ID,rcfk.COMPANY_ID,cp.COMPANY_NAME,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfkd.PAY_FUNDS_TOTAL-rcfk.TRANSFER-rcfk.MARKET_DEDICATED else rcfkd.PAY_FUNDS_TOTAL-cast((rcfk.TRANSFER*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4))-cast((rcfk.MARKET_DEDICATED*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end)
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfkd.PAY_FUNDS_TOTAL-rcfk.TRANSFER-rcfk.MARKET_DEDICATED else rcfkd.PAY_FUNDS_TOTAL-cast((rcfk.TRANSFER*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4))-cast((rcfk.MARKET_DEDICATED*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) else 0 end,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.TAX else cast((rcfk.TAX*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.TAX else cast((rcfk.TAX*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) else 0 end,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.PACKAGING_MATERIALS else cast((rcfk.PACKAGING_MATERIALS*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.PACKAGING_MATERIALS else cast((rcfk.PACKAGING_MATERIALS*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) else 0 end,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.SPARE_PARTS else cast((rcfk.SPARE_PARTS*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.SPARE_PARTS else cast((rcfk.SPARE_PARTS*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end)else 0 end,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.COAL else cast((rcfk.COAL*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.COAL else cast((rcfk.COAL*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end)else 0 end,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.STEAM_ELECTRICITY else cast((rcfk.STEAM_ELECTRICITY*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.STEAM_ELECTRICITY else cast((rcfk.STEAM_ELECTRICITY*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end)else 0 end,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.SALARY else cast((rcfk.SALARY*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.SALARY else cast((rcfk.SALARY*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end)else 0 end,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.FREIGHT else cast((rcfk.FREIGHT*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.FREIGHT else cast((rcfk.FREIGHT*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end)else 0 end,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.ELSE_PROJ else cast((rcfk.ELSE_PROJ*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.ELSE_PROJ else cast((rcfk.ELSE_PROJ*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end)else 0 end,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.MARKET_DEDICATED else cast((rcfk.MARKET_DEDICATED*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.MARKET_DEDICATED else cast((rcfk.MARKET_DEDICATED*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end)else 0 end,
        case when (rcfkd.PAY_WAY='O' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.TRANSFER else cast((rcfk.TRANSFER*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end) 
        when (status.TMS_STATUS='4' and rcfk.TERMINATE_FLAG='N') then (case when rcfkd.PAY_FUNDS_TOTAL >= rcfk.AMOUNT then rcfk.TRANSFER else cast((rcfk.TRANSFER*rcfkd.PAY_FUNDS_TOTAL)/rcfk.AMOUNT as decimal(10,4)) end)else 0 end,
        rcfk.PAYMENT_DATE FROM PROC_DAILY_PAY_LOAN_TRAN rcfk join COMPANY cp on(rcfk.COMPANY_ID=cp.ID) left join PROC_DAILY_PAY_DETAIL rcfkd on(rcfkd.PROC_INST_ID=rcfk.ID)
       left join PROC_TMS_STATUS status on(status.PAY_ID=rcfkd.ID)
);

---------公司+品项 申请类型------------
drop view vw_stat_companypx_sq;
create view vw_stat_companypx_sq(ptype,procId,companyId,payDate,tzlc,lxyh,sdck,rzzk,rzfzk,hdzk,hdfzk,gck,gl,ghgd,zjgl,zjfgl,rcfk) as(
--投资、融资保证金及归还银行贷款借款/转款
--表单类型'I'--投资理财（纯理财）,'F'--融资保证金,'R'--还贷,'C'--利息及融资费用
SELECT 'tzrz',tzrz.ID,tzrz.COMPANY_ID,tzrz.PAYMENT_DATE,
        sum(case when (tzrz.FORM_TYPE='I' and tzrz.TERMINATE_FLAG='N') then tzrz.AMOUNT else 0 end) as tzlc,
        sum(case when (tzrz.FORM_TYPE='C' and tzrz.TERMINATE_FLAG='N') then tzrz.AMOUNT else 0 end) as lxyh,
        sum(case when (tzrz.FORM_TYPE='D' and tzrz.TERMINATE_FLAG='N') then tzrz.AMOUNT else 0 end) as sdck,
        sum(case when (tzrz.FORM_TYPE='F' and tzrz.TRANSIT='Y' and tzrz.TERMINATE_FLAG='N') then tzrz.AMOUNT else 0 end) as rzzk,
        sum(case when (tzrz.FORM_TYPE='F' and tzrz.TRANSIT='N' and tzrz.TERMINATE_FLAG='N') then tzrz.AMOUNT else 0 end) as rzfzk,
        sum(case when (tzrz.FORM_TYPE='R' and tzrz.TRANSIT='Y' and tzrz.TERMINATE_FLAG='N') then tzrz.AMOUNT else 0 end) as hdzk,
        sum(case when (tzrz.FORM_TYPE='R' and tzrz.TRANSIT='N' and tzrz.TERMINATE_FLAG='N') then tzrz.AMOUNT else 0 end) as hdfzk,
        0 as gck, 0 as gl, 0 as ghgd, 0 as zjgl,0 as zjfgl,0 as rcfk
        FROM  PROC_INVE_FINA_BAIL tzrz 
        group by 'tzrz',tzrz.ID,tzrz.COMPANY_ID,tzrz.PAYMENT_DATE
union all
--付工程款股利及归还股东借款流程
SELECT 'gck',gck.ID,gck.COMPANY_ID,gck.PAYMENT_DATE,
        0 as tzlc, 0 as lxyh, 0 as sdck, 0 as rzzk, 0 as rzfzk, 0 as hdzk, 0 as hdfzk,
        sum(case when (gck.FORM_TYPE='E' and gck.TERMINATE_FLAG='N') then gck.AMOUNT else 0 end) as fgck,
        sum(case when (gck.FORM_TYPE='D' and gck.TERMINATE_FLAG='N') then gck.AMOUNT else 0 end) as gl,
        sum(case when (gck.FORM_TYPE='A' and gck.TERMINATE_FLAG='N') then gck.AMOUNT else 0 end) as ghgd,
        0 as zjgl,0 as zjfgl,0 as rcfk
        FROM  PROC_PAY_PROJECT  gck 
        group by  'gck',gck.ID,gck.COMPANY_ID,gck.PAYMENT_DATE
union all
--采购资金（贸易）借款/转款流程
SELECT 'zjmy',zjmy.ID,zjmy.COMPANY_ID,zjmy.PAYMENT_DATE,
        0 as tzlc, 0 as lxyh, 0 as sdck, 0 as rzzk, 0 as rzfzk, 0 as hdzk, 0 as hdfzk, 0 as gck, 0 as gl, 0 as ghgd,
        sum(case when (px.VARIETY_RELATED='Y' and zjmy.TERMINATE_FLAG='N') then px.VARIETY_AMOUNT else 0 end)  as zjgl,
        sum(case when (px.VARIETY_RELATED='N' and zjmy.TERMINATE_FLAG='N') then px.VARIETY_AMOUNT else 0 end)  as zjfgl,
        0 as rcfk
        from 
         PROC_PURCHASE_FUND_TRADE zjmy left  join PROC_PURCHASE_FUND_TRADE_PRODUCT px  on(zjmy.ID=px.PROC_INST_ID)  
        group by  'zjmy',zjmy.ID,zjmy.COMPANY_ID,zjmy.PAYMENT_DATE
union all
--采购资金（生产）借款/转款流程  
SELECT 'zjsc',zjsc.ID,zjsc.COMPANY_ID,zjsc.PAYMENT_DATE,
        0 as tzlc, 0 as lxyh, 0 as sdck, 0 as rzzk, 0 as rzfzk, 0 as hdzk, 0 as hdfzk, 0 as gck, 0 as gl, 0 as ghgd,
        sum(case when (px.VARIETY_RELATED='Y' and zjsc.TERMINATE_FLAG='N') then px.VARIETY_AMOUNT else 0 end) as zjgl,
        sum(case when (px.VARIETY_RELATED='N' and zjsc.TERMINATE_FLAG='N') then px.VARIETY_AMOUNT else 0 end) as zjfgl,
        0 as rcfk
        from 
        PROC_PURCHASE_FUND_PROD zjsc left join PROC_PURCHASE_FUND_PROD_PRODUCT px  on(zjsc.ID=px.PROC_INST_ID)  
        group by  'zjsc',zjsc.ID,zjsc.COMPANY_ID,zjsc.PAYMENT_DATE
union all
--日常付款借款/转款流程
SELECT 'rcfk',rcfk.ID,rcfk.COMPANY_ID,rcfk.PAYMENT_DATE,
        0 as tzlc, 0 as lxyh, 0 as sdck, 0 as rzzk, 0 as rzfzk, 0 as hdzk, 0 as hdfzk, 0 as gck, 0 as gl, 0 as ghgd,0 as zjgl,0 as zjfgl,
        sum(case when rcfk.TERMINATE_FLAG='N' then rcfk.AMOUNT-rcfk.TRANSFER-rcfk.MARKET_DEDICATED else 0 end) as rcfk
        FROM COMPANY cp join PROC_DAILY_PAY_LOAN_TRAN  rcfk on(cp.ID=rcfk.COMPANY_ID)
        where  rcfk.TERMINATE_FLAG='N'
        group by 'rcfk',rcfk.ID,rcfk.COMPANY_ID,rcfk.PAYMENT_DATE
);

-------公司-贸易/生产的关联/非关联  申请类型------
drop view vw_stat_zjmysc_sq;
create view vw_stat_zjmysc_sq(type,pxKey,pxName,related,porcId,cpId,sjxb,payDate) as(
SELECT 'zjmy',dc.CODE_KEY,dc.CODE_VAL,px.VARIETY_RELATED,payd.ID,zjmy.COMPANY_ID,
        sum(case when zjmy.TERMINATE_FLAG='N' then px.VARIETY_AMOUNT else 0 end),zjmy.PAYMENT_DATE
        from PROC_PURCHASE_FUND_TRADE_PRODUCT px join DICT dc on(dc.CODE_KEY=px.VARIETY and (dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.FOOD' or dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.OIL') ) 
        left join PROC_PURCHASE_FUND_TRADE zjmy on(zjmy.ID=px.PROC_INST_ID) left join PROC_PURCHASE_FUND_TRADE_PAY pay on(pay.PROC_INST_ID=zjmy.ID)
        left join PROC_PURCHASE_FUND_TRADE_PAY_DETAIL payd on(payd.PAY_ID = pay.ID and payd.PRODUCT_ID=px.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=pay.ID) 
        where  zjmy.TERMINATE_FLAG='N'
        group by 'zjmy','zjmy',dc.CODE_KEY,dc.CODE_VAL,px.VARIETY_RELATED,payd.ID,zjmy.COMPANY_ID,zjmy.PAYMENT_DATE
union all
--采购资金（生产）借款/转款流程        
SELECT 'zjsc',dc.CODE_KEY ,dc.CODE_VAL,px.VARIETY_RELATED,payd.ID,zjsc.COMPANY_ID,
        sum(case when zjsc.TERMINATE_FLAG='N' then px.VARIETY_AMOUNT else 0 end),zjsc.PAYMENT_DATE 
        from PROC_PURCHASE_FUND_PROD_PRODUCT px join DICT dc on(dc.CODE_KEY=px.VARIETY and (dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.FOOD' or dc.CODE_CAT='TMS.FARM.PRODUCE.VARIETY.TYPE.OIL') ) 
        left join PROC_PURCHASE_FUND_PROD zjsc on(zjsc.ID=px.PROC_INST_ID) left join PROC_PURCHASE_FUND_PROD_PAY pay on(pay.PROC_INST_ID=zjsc.ID)
        left join PROC_PURCHASE_FUND_PROD_PAY_DETAIL payd on(payd.PAY_ID = pay.ID and payd.PRODUCT_ID=px.ID) left join PROC_TMS_STATUS status on(status.PAY_ID=pay.ID)
        where  zjsc.TERMINATE_FLAG='N'
        group by 'zjsc',dc.CODE_KEY ,dc.CODE_VAL,px.VARIETY_RELATED,payd.ID,zjsc.COMPANY_ID,zjsc.PAYMENT_DATE
);

---日常付款：明细   申请类型----
drop view vw_stat_rcfk_sq;
create view vw_stat_rcfk_sq(procId,cpId,cpName,total,tax,packMater,sperPart,coal,steam,salary,freight,elseProj,marketDedicted,transfer,payDate) as(
SELECT rcfk.ID,rcfk.COMPANY_ID,cp.COMPANY_NAME,
        rcfk.AMOUNT-rcfk.MARKET_DEDICATED-rcfk.TRANSFER,
        rcfk.TAX,
        rcfk.PACKAGING_MATERIALS,
        rcfk.SPARE_PARTS,
        rcfk.COAL,
        rcfk.STEAM_ELECTRICITY,
        rcfk.SALARY,
        rcfk.FREIGHT,
        rcfk.ELSE_PROJ,
        rcfk.MARKET_DEDICATED,
        rcfk.TRANSFER,
        rcfk.PAYMENT_DATE FROM PROC_DAILY_PAY_LOAN_TRAN rcfk join COMPANY cp on(rcfk.COMPANY_ID=cp.ID) 
       where  rcfk.TERMINATE_FLAG='N'
);