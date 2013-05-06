package ru.spb.locon

class ReviewsEntity {

  String fio
  String email
  String content
  Date date

  static mapping = {

    datasource 'ALL'

    table: 'filter'
    columns {
      id column: 'reviews_id'
      fio column: 'reviews_fio'
      email column: 'reviews_email'
      content column: 'reviews_content'
      date column: 'reviews_date'
    }

    version false

  }

  static constraints = {
    fio nullable: false
    email nullable: true
    content nullable: false
    date nullable: false
  }

}
