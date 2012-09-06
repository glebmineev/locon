import ru.spb.locon.ProductPropertyEntity
import ru.spb.locon.CategoryEntity
import ru.spb.locon.ManufacturerEntity

class BootStrap {

    def init = { servletContext ->

        ProductPropertyEntity.withTransaction {
            ProductPropertyEntity.findOrSaveWhere(name: "Объем")
        }

        CategoryEntity.withTransaction {
            CategoryEntity.findOrSaveWhere(name: "Для волос")
            CategoryEntity.findOrSaveWhere(name: "Для тела")
            CategoryEntity.findOrSaveWhere(name: "Для рук")
            CategoryEntity.findOrSaveWhere(name: "Депиляция")
        }

        ManufacturerEntity.withTransaction {
            ManufacturerEntity.findOrSaveWhere(name: "Индола")
            ManufacturerEntity.findOrSaveWhere(name: "Periche")
            ManufacturerEntity.findOrSaveWhere(name: "Hair light")
            ManufacturerEntity.findOrSaveWhere(name: "OLLIN Professional")
            ManufacturerEntity.findOrSaveWhere(name: "Schwarzkopf professional")
            ManufacturerEntity.findOrSaveWhere(name: "Screen")
        }
    }

    def destroy = {
    }
}
