CREATE TABLE IF NOT EXISTS USERS
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    email VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    requestor_id INTEGER REFERENCES USERS (id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS ITEMS
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    description VARCHAR(200) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id INTEGER REFERENCES USERS (id) ON DELETE CASCADE,
    request_id INTEGER REFERENCES REQUESTS (id),
    CONSTRAINT UQ_OWNER_ITEM_NAME UNIQUE(owner_id, name)
);

CREATE TABLE IF NOT EXISTS BOOKINGS
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id INTEGER REFERENCES ITEMS (id) ON DELETE CASCADE,
    booker_id INTEGER REFERENCES USERS (id) ON DELETE CASCADE,
    status VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(255) NOT NULL,
    item_id INTEGER REFERENCES ITEMS (id) ON DELETE CASCADE,
    author_id INTEGER REFERENCES USERS (id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);