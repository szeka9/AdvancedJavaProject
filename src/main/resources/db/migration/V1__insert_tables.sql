CREATE TABLE public.toolpathsharing_user (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY,
    email_address character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    role character varying(255) NOT NULL,
    password bytea NOT NULL,
    CONSTRAINT toolpathsharing_user_email_address_key UNIQUE (email_address),
    CONSTRAINT toolpathsharing_user_name_key UNIQUE (name),
    CONSTRAINT toolpathsharing_user_pkey PRIMARY KEY (id)
);

CREATE TABLE public.toolpathsharing_group (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY,
    managing_user_id integer,
    name character varying(255) NOT NULL,
    CONSTRAINT toolpathsharing_group_name_key UNIQUE (name),
    CONSTRAINT toolpathsharing_group_pkey PRIMARY KEY (id),
    CONSTRAINT fk2bf39glu3k4camub8kex11cf9 FOREIGN KEY (managing_user_id) REFERENCES public.toolpathsharing_user(id)
);

CREATE TABLE public.toolpathsharing_usergroup_user (
    group_id integer NOT NULL,
    user_id integer NOT NULL,
    CONSTRAINT toolpathsharing_usergroup_user_pkey PRIMARY KEY (group_id, user_id),
    CONSTRAINT fkdl7frh4jkrnwk7hmtoruggwc9 FOREIGN KEY (user_id) REFERENCES public.toolpathsharing_user(id),
    CONSTRAINT fkmawctncns5msddkl7bqpqqk6s FOREIGN KEY (group_id) REFERENCES public.toolpathsharing_group(id)
);

CREATE TABLE public.machine (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY,
    machine_type smallint,
    workspace_depth double precision,
    workspace_height double precision,
    workspace_width double precision,
    manufacturer character varying(255),
    name character varying(255) NOT NULL,
    picture_uri character varying(255),
    CONSTRAINT machine_machine_type_check CHECK (((machine_type >= 0) AND (machine_type <= 2))),
    CONSTRAINT machine_name_key UNIQUE (name),
    CONSTRAINT machine_pkey PRIMARY KEY (id)
);

CREATE TABLE public.machinetool (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id integer,
    name character varying(255) NOT NULL,
    supported_materials character varying(255)[],
    CONSTRAINT machinetool_name_key UNIQUE (name),
    CONSTRAINT machinetool_pkey PRIMARY KEY (id),
    CONSTRAINT fkh9aydc3pvp1xehx3df1nyy5um FOREIGN KEY (user_id) REFERENCES public.toolpathsharing_user(id)
);

CREATE TABLE public.machine_machinetool (
    machine_id integer NOT NULL,
    machinetool_id integer NOT NULL,
    CONSTRAINT machine_machinetool_pkey PRIMARY KEY (machine_id, machinetool_id),
    CONSTRAINT fk11fht6lulvhdlcbky60rb90du FOREIGN KEY (machine_id) REFERENCES public.machine(id),
    CONSTRAINT fkcu93alm2pxg9bgfk2dwc7jcyp FOREIGN KEY (machinetool_id) REFERENCES public.machinetool(id)
);

CREATE TABLE public.toolpath (
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY,
    group_id integer,
    is_public boolean NOT NULL,
    user_id integer,
    date_of_creation timestamp(6) without time zone NOT NULL,
    file_uri character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT toolpath_pkey PRIMARY KEY (id),
    CONSTRAINT fkeocv8j61l8qkdr9onj4340yca FOREIGN KEY (group_id) REFERENCES public.toolpathsharing_group(id),
    CONSTRAINT fkqjbeonrqfqovnpswo4y5tfrp4 FOREIGN KEY (user_id) REFERENCES public.toolpathsharing_user(id)
);
