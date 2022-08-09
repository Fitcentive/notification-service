# -- !Ups

-- Add invisible_filters field
alter table notification_data
add column has_been_viewed boolean not null default false;

# -- !Downs
