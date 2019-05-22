create table rates (
    curr text not null
         constraint rates_curr_pk
            primary key,
    rate numeric(20, 8) not null
);