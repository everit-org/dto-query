# dto-query
API to query whole DTO hierarchies effectively with Querydsl SQL.

## Usage

### About sample tables

We have two tables and two DTOs with the same public fields:

TableA
 - tableAId
 - tableAString
 - tableBList (only in the DTO)

TableB
 - tableBId
 - tableBString
 - tableAId (foreign key to table A)

### Low level API

      QTableA tableA = QTableA.tableA;
      QTableB tableB = QTableB.tableB;
      QTableC tableC = QTableC.tableC;
    
      DTOQuery<Object, TableADTO> dtoQuery = DTOQuery
          .root(new SQLQuery<>()
              .select(Projections.fields(TableADTO.class, tableA.all()))
              .from(tableA)
              .orderBy(tableA.tableAId.asc()))
    
          .prop(new PropertyQuery<TableADTO, Long, TableBDTO>()
              .oneToManySetter((aDTO, bDTOList) -> aDTO.tableBList = bDTOList)
              .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
              .keyInPropertyDTOResolver((tableBDTO) -> tableBDTO.tableAId)
              .dtoQuery(DTOQuery
                  .create((Collection<Long> fks) -> new SQLQuery<>()
                      .select(Projections.fields(TableBDTO.class, tableB.all()))
                      .from(tableB)
                      .where(tableB.tableAId.in(fks))
                      .orderBy(tableB.tableBId.asc()))))));
    
      Collection<TableADTO> tableACollection = dtoQuery.queryDTO(connection);

In the code above a DTOQuery is created. The SQL Query is passed to the
 DTOQuery that selects the necessary DTOs. With the prop function, it is
 specified that a DTO property of the dto should be queried from the
 database, too.

The prop function accepts a PropertyQuery parameter where the following
parameters must be passed using the builder pattern:

 - **oneToManySetter (or setter if it is not a collection):** A lambda
   expression that sets the queried property DTO(s) to the source DTO.
 - **keyInSourceDTOResolver:** A lambda expression that resolves the
   key in the source DTO.
 - **keyInPropertyDTOResolver:** A lambda expression that resolves the
   key in the property DTO.
 - **dtoQuery:** A DTO query for the property. Please note that here a
   _queryGenerator_ is specified instead of using the _root()_ function
   of DTOQuery. For properties, this queryGenerator receives the set of
   foreign keys that the generated SQL query can use to filter the database.
   The DTOQuery instance of the property can also have one or more _prop()_
   function calls, to it can child DTOs, too.

### Using the *fullTable functions

There are some helper functions in DTOQuery that can be used if the table
and the DTO have the same fields with the same names. The DTO can, of course,
have additional child DTOs.

The naming conventions for the _*fullTable_ functions is

 - **beanFullTable:** The _Projections.bean(...)_ function is used in the
     SQLQuery instance, so setters of the properties will be used.
 - **beanFullTable:** The _Projections.fields(...)_ function is used in the
     SQLQuery instance, so the fields in the DTOs must be public ones.

Using these functions, the code above can be reduced like this:

      QTableA tableA = QTableA.tableA;
      QTableB tableB = QTableB.tableB;
      QTableC tableC = QTableC.tableC;
    
      DTOQuery<Object, TableADTO> dtoQuery = DTOQuery
          .rootDTOFullTable(configuration, tableA, TableADTO.class)
    
          .prop(new PropertyQuery<TableADTO, Long, TableBDTO>()
              .oneToManySetter((aDTO, bDTOList) -> aDTO.tableBList = bDTOList)
              .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
              .keyInPropertyDTOResolver((tableBDTO) -> tableBDTO.tableAId)
              .dtoQuery(
                  DTOQuery.dtoFullTable(configuration, tableB, TableBDTO.class, tableB.tableAId)
                      .prop(new PropertyQuery<TableBDTO, Long, TableCDTO>()
                          .oneToManySetter((bDTO, cDTOList) -> bDTO.tableCList = cDTOList)
                          .keyInSourceDTOResolver(bDTO -> bDTO.tableBId)
                          .keyInPropertyDTOResolver((cDTO) -> cDTO.tableBId)
                          .dtoQuery(DTOQuery.dtoFullTable(configuration, tableC, TableCDTO.class,
                              tableC.tableBId)))));
    
      Collection<TableADTO> tableACollection = dtoQuery.queryDTO(connection);


### Using DTOWithKeys

There are two helper classes to select props where the property type not
necessarily contains the foreign key:

 - QDTOWithKeys that can be used in the select part of the query.
 - DTOWithKeys is the class that is polled if QDTOWithKeys is used
   in the query.


#### Using DTOWithKeys with SimpleExpression

In the sample above, TableADTO has a Collection<String> property that should
contain the list of TableB.tableBvc values:

      DTOQueryTest.qdsl.execute((connection, configuration) -> {
        QTableA tableA = QTableA.tableA;
        QTableB tableB = QTableB.tableB;

        DTOQuery<Object, TableAWithStringCollectionDTO> dtoQuery = DTOQuery
            .root(new SQLQuery<>()
                .select(Projections.fields(TableAWithStringCollectionDTO.class, tableA.all()))
                .from(tableA)
                .orderBy(tableA.tableAId.asc()))

            .prop(new PropertyQuery<TableAWithStringCollectionDTO, Long, DTOWithKeys<String>>()
                .oneToManySetter((aDTO, dtoWithKeys) -> aDTO.tableBVcList =
                    dtoWithKeys.stream().map(e -> e.dto).collect(Collectors.toList()))
                .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
                .keyInPropertyDTOResolver((dtoWithKeys) -> dtoWithKeys.keys.get(tableB.tableAId))
                .dtoQuery(DTOQuery
                    .create((Collection<Long> fks) -> new SQLQuery<>()
                        .select(new QDTOWithKeys<>(tableB.tableBVc, tableB.tableAId))
                        .from(tableB)
                        .where(tableB.tableAId.in(fks))
                        .orderBy(tableB.tableBId.asc()))

                ));


#### Using DTOWithKeys with a DTO

It is also possible to select a property where the property type is
a DTO but the DTO itself does not contain the foreign key. In the
following sample, the tableB is selected with the help of DTOWithKeys,
so the TableBDTO does not necessarily has to contain the tableAId:

    DTOQuery<Object, TableADTO> dtoQuery = DTOQuery
        .rootDTOFullTable(configuration, tableA, TableADTO.class)

        .prop(new PropertyQuery<TableADTO, Long, DTOWithKeys<TableBDTO>>()
            .oneToManySetter((aDTO, b) -> aDTO.tableBList =
                b.stream().map(e -> e.dto).collect(Collectors.toList()))
            .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
            .keyInPropertyDTOResolver((b) -> b.keys.get(tableB.tableAId))
            .dtoQuery(
                DTOQuery
                    .create((Collection<Long> tableAIds) -> new SQLQuery<DTOWithKeys<TableBDTO>>(
                        connection, configuration)
                            .select(
                                new QDTOWithKeys<>(
                                    Projections.fields(TableBDTO.class, tableB.all()),
                                    tableB.tableAId))
                            .from(tableB).where(tableB.tableAId.in(tableAIds)))
                    .prop(new PropertyQuery<DTOWithKeys<TableBDTO>, Long, TableCDTO>()
                        .oneToManySetter((bDTO, cDTOList) -> bDTO.dto.tableCList = cDTOList)
                        .keyInSourceDTOResolver(bDTO -> bDTO.dto.tableBId)
                        .keyInPropertyDTOResolver((cDTO) -> cDTO.tableBId)
                        .dtoQuery(DTOQuery.dtoFullTable(configuration, tableC, TableCDTO.class,
                            tableC.tableBId)))));
