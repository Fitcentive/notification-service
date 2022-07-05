# -- !Ups

-- Constraint tables
create table notification_types (
    name varchar not null constraint pk_notification_types primary key,
    description varchar not null
);

insert into notification_types (name, description) values ('UserFollowRequest', 'Notification type when user A requests to follow user B');

create table notification_data (
    id uuid not null constraint pk_notification_data primary key,
    target_user uuid not null,
    notification_type varchar not null constraint fk_notification_type references notification_types,
    is_interactive boolean not null default false,
    has_been_interacted_with boolean not null default false,
    data jsonb,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

# -- !Downs