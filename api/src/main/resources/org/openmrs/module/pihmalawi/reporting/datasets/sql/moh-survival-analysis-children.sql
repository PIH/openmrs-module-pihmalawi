DROP TEMPORARY TABLE IF EXISTS survival_analysis;

    CREATE TEMPORARY TABLE survival_analysis(
		id INT PRIMARY KEY auto_increment,
		reporting_year int,
		reporting_quarter int,
        subgroup varchar(150),
        new_reg int,
		active int,
        died int,
        defaulted int,
        treatment_stopped int,
        transferred_out int,
        location varchar(50)
	) DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


SET @subgroup = "Children";
SET @ageLimit = 14;
SET @defaultCutOff = 60;

call create_survival_analysis(@endDate,@startDate,@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 1 year),DATE_SUB(@startDate, INTERVAL 1 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 2 year),DATE_SUB(@startDate, INTERVAL 2 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 3 year),DATE_SUB(@startDate, INTERVAL 3 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 4 year),DATE_SUB(@startDate, INTERVAL 4 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 5 year),DATE_SUB(@startDate, INTERVAL 5 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 7 year),DATE_SUB(@startDate, INTERVAL 7 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 8 year),DATE_SUB(@startDate, INTERVAL 8 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 9 year),DATE_SUB(@startDate, INTERVAL 9 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 10 year),DATE_SUB(@startDate, INTERVAL 10 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 11 year),DATE_SUB(@startDate, INTERVAL 11 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 12 year),DATE_SUB(@startDate, INTERVAL 12 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 13 year),DATE_SUB(@startDate, INTERVAL 13 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 14 year),DATE_SUB(@startDate, INTERVAL 14 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 15 year),DATE_SUB(@startDate, INTERVAL 15 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 16 year),DATE_SUB(@startDate, INTERVAL 16 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 17 year),DATE_SUB(@startDate, INTERVAL 17 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 18 year),DATE_SUB(@startDate, INTERVAL 18 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 19 year),DATE_SUB(@startDate, INTERVAL 19 year),@ageLimit,@location,@subgroup,@defaultCutOff);
call create_survival_analysis(DATE_SUB(@endDate, INTERVAL 20 year),DATE_SUB(@startDate, INTERVAL 20 year),@ageLimit,@location,@subgroup,@defaultCutOff);

 delete from survival_analysis where reporting_year <= ((select year(min(visit_date)) from mw_art_initial where location = @location));

 SELECT
    reporting_year,
    reporting_quarter,
    CONCAT("Q",QUARTER(@endDate)," ",reporting_year) as reg_cohort,
    ((year(curdate())-reporting_year)*12) as interval_year,
    subgroup,
    SUM(active+died+defaulted+treatment_stopped+transferred_out) as total_reg_database,
    max(active) as alive,
    max(died) as died,
    max(defaulted) as defaulted,
    max(treatment_stopped) as stopped,
    max(transferred_out) as "to"
FROM
    survival_analysis
GROUP BY
    reporting_year;