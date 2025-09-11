-- I.e., 'subaccount'
CREATE TABLE purchasing_account (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
);

-- E.g., grant/project/program associated
CREATE TABLE program (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
);

CREATE TABLE supplier (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
);

CREATE TABLE category (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
);

CREATE TABLE item (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    weight_hundredths INTEGER NOT NULL, -- Stores with 2 decimals of precision (multiple by 100 for value)
    weight_units TEXT CHECK( weight_units IN ('OZ','LB') ) NOT NULL -- One of set of possible units (bc application needs to implement conversions)
);

-- items --[1:many]--> category
CREATE TABLE item_category (
    id INTEGER PRIMARY KEY,
    FOREIGN KEY(item_id) REFERENCES item(id) NOT NULL,
    FOREIGN KEY(category_id) REFERENCES category(id) NOT NULL,

    UNIQUE(item_id, category_id)
);

CREATE TABLE entry (
    id INTEGER PRIMARY KEY,
    cost_status TEXT CHECK( type IN ('PURCHASED', 'NO_COST') ) NOT NULL,
    unit_cost_cents INTEGER NOT NULL, -- Applies to 'item's by default; 'aggregate' when not null

    FOREIGN KEY(item_id) REFERENCES item(id) NOT NULL,
    item_count INTEGER NOT NULL,

    -- Aggregation of items
    aggregate_type TEXT CHECK( type IN ('CASE') ),
    aggregate_count INTEGER,

    -- Aggregation are optional as a pair--either both present or both null
    CHECK((aggregate_type == NULL AND aggregate_count == NULL) OR (aggregate_type != NULL AND aggregate_count != NULL))
);

CREATE TABLE entry_purchasing_account (
    id INTEGER PRIMARY KEY,
    FOREIGN KEY(entry_id) REFERENCES entry(id) NOT NULL,
    FOREIGN KEY(purchasing_account_id) REFERENCES purchasing_account(id) NOT NULL,
    percentage_hundredths INTEGER NOT NULL,
);

CREATE TABLE entry_program (
    id INTEGER PRIMARY KEY,
    FOREIGN KEY(entry_id) REFERENCES entry(id) NOT NULL,
    FOREIGN KEY(program_id) REFERENCES program(id) NOT NULL,
    percentage_hundredths INTEGER NOT NULL,
    -- (entry, program) pair should be unique, although unsure if worth enforcing ...
);

CREATE TABLE delivery (
    id INTEGER PRIMARY KEY,
    received_date TEXT NOT NULL, -- ISO-8601 date, e.g., "2025-09-11"
    FOREIGN KEY(supplier_id) REFERENCES supplier(id),
    taxes_cents INTEGER NOT NULL,
    fees_cents INTEGER NOT NULL,
);

-- links to 1 or more entry’s
CREATE TABLE delivery_entry (
    id INTEGER PRIMARY KEY,
    FOREIGN KEY(delivery_id) REFERENCES delivery(id) NOT NULL,
    FOREIGN KEY(entry_id) REFERENCES entry(id) NOT NULL, -- This should be unique !
);