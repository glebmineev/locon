package ru.spb.locon

class MessageEntity {

  String title
  String from
  String content
  Date date = new Date()

  UserEntity user

  static mapping = {

    datasource 'ALL'

    table: 'message'
    columns {
      id column: 'message_id'
      title column: 'message_title'
      from column: 'message_from'
      content column: 'message_content'
      date column: 'message_date'
      user fetch: "join", column: 'order_user_id'
    }

    version false

  }

  static constraints = {
    title nullable: true
    from nullable: true
    content nullable: true
    date nullable: true
    user nullable: true
  }

}
