# drugs.csv renames this already-retired duplicate drug to disambiguate it from the active
# "Metronidazole (250mg tablet)" drug that shares its exact name (MLW-1839). But
# openmrs-module-initializer's CsvParser.shouldFill() skips the entire fill step - including the
# name change - for any CSV row whose Void/Retire column is true when the bootstrapped entity
# already exists (has a non-null id). Since this drug is already retired in production, the rename
# silently never applies there (it only works on a fresh install, where the entity doesn't exist
# yet and shouldFill() behaves differently). Apply the rename directly here instead, before the
# drugs domain loads - a no-op on a fresh install where the drug doesn't exist yet, since the
# WHERE clause simply won't match anything until Initializer creates it (with the correct name,
# taken directly from drugs.csv on that first creation).

update drug set name = 'Metronidazole (250mg tablet) (retired)'
where uuid = '1895edd8-a89d-11df-bba5-000c297f1161'
and name != 'Metronidazole (250mg tablet) (retired)';
