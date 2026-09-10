# Production has accumulated duplicate htmlformentry_html_form rows for 14 forms - multiple rows
# sharing the same form_id, left over from historical re-imports/re-saves. openmrs-module-
# initializer's HtmlFormsLoader looks up the existing HtmlForm only by its parent Form
# (getHtmlFormByForm), not by its own uuid, so when more than one row exists for the same form it
# picks one arbitrarily and then tries to overwrite its uuid with our config's value - which
# collides with whichever other duplicate row already legitimately owns that uuid, aborting the
# htmlforms domain load entirely (MLW-1839).
#
# Nothing else in the schema references htmlformentry_html_form by foreign key (verified via
# information_schema.KEY_COLUMN_USAGE) - real clinical data (encounters, etc.) links to
# form.form_id, which every duplicate row below shares with the row being kept and which is never
# touched here. No migration of any other table's data is needed.
#
# Each form's "keep" uuid was identified by matching our configuration/configuration/htmlforms
# file's own htmlformUuid to whichever production row was still being individually edited years
# after the others (a distinctive per-form later edit date, vs. the abandoned rows' one-time or
# no edits since 2021-2022) - see MLW-1839 for the full analysis. The other row(s) for that same
# form_id are deleted below.

delete from htmlformentry_html_form where uuid in (
    -- Chronic Kidney Disease Imaging Results (form_id 99) - keep 8a6e5898-b100-4b32-98a4-8783670b9db7
    '3a6d0bd5-145b-4e8f-9ecf-73c1f0d183c8',

    -- PDC Developmental Delay eMastercard (form_id 110) - keep 6e2641a1-1b97-481b-99cc-e64c2a5e6e37
    '85c55318-abad-44c4-a70a-60ec27f44c8d',
    '27947dbd-03ab-4bb9-9050-a280720071c4',

    -- Developmental Delay Visit (form_id 111) - keep b52b4a52-5991-4518-8e20-3b7556faf16e
    'e86412cf-d2e4-40aa-92f6-d15a5ce9c406',
    '14d716c9-590a-4b37-8ab8-b8d1b551a3ef',

    -- Cleft Lip / Palate Visit (form_id 112) - keep 415a383e-6795-4a5f-8409-d2e5f648cc4e
    '999d544e-318c-49c6-aa56-7742bffffe5b',
    '7f49023f-9bb0-45d2-9c7c-2587afd682a7',

    -- PDC Cleft Lip / Palate eMastercard (form_id 113) - keep dc67e15a-3ed9-4d7c-87cc-58751bbda7c5
    '5bf73d46-2592-4fdf-92c4-35bce9e3b3b0',
    'a48ee4e4-711c-4c5d-b9a4-93814a4d1fb8',

    -- PDC Trisomy eMastercard (form_id 114) - keep 5023eb54-8f70-4645-b448-ef99d9ef0b38
    'ae0b2ee9-7c4e-45c3-8758-8e65b9523ff5',
    'ae11ee91-9943-45b1-a808-500939e2337b',

    -- PDC Trisomy 21 Visit (form_id 115) - keep 22725aa2-b6ae-46c5-96d8-816471e4751c
    '22f6ee23-65bd-49f8-b66c-7c6103aa2313',
    '84183920-642f-4ba9-927b-ec606e8f65e8',

    -- Other Diagnosis Visit (form_id 116) - keep 91bcd778-1bdd-4ebe-ab71-944edf34ebad
    '626915f1-9fbe-4493-8924-8364f289c017',
    '30c72a9f-b44c-405a-bf40-3899e2ee2e30',

    -- PDC Other Diagnosis eMastercard (form_id 117) - keep 654df1c2-3731-4814-b201-80e3c76b1ebb
    'b1808b65-3030-47d0-933b-af572c20d9bd',
    'd679d4a3-5745-4b32-93b9-9f5ce3dab4b8',

    -- PDC Hospitalization History (form_id 118) - keep 8dafcf79-7eae-446e-847d-ca1ea1322e06
    '281f9fc8-1072-48a6-802a-f322f3998cd5',
    '32793b9a-4376-49a5-a96b-767cdcc20ab3',

    -- Vision Test (form_id 119) - keep 9fca8ed6-01fd-4fec-96fc-1b0f9ad60494
    '0be7f45a-81d9-4f3c-a926-87a87083051b',
    'eae845cc-146c-4ea4-9d07-cbb10915a1d5',

    -- PDC Hearing Test (form_id 120) - keep 4b25f8c5-2e31-496b-af87-de78e681b7d5
    '61091748-1041-4ac8-bf1d-2fb1de3be37a',
    '29164d2f-0cb4-4515-8e15-ffd966533ab3',

    -- PDC Radiology Screening (form_id 121) - keep 28ec97e0-cda4-4cf7-9504-4c9f194edd7f
    '79d35b5d-89a7-4d30-baa7-b23559e2ce35',
    'fb00bdd7-0ec5-4581-9c63-89a880dc1452',

    -- PDC Complications (form_id 122) - keep 2a6fd929-b8af-4f7e-be31-a1ebc5035faf
    'a646c3cd-e2a0-4c9d-801b-1b3857bd0cd4',
    'ef2a6c78-35ee-460b-b7b0-f9a94724df65'
);
