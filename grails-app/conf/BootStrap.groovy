import locon.ProductPropertyEntity
import locon.CategoryEntity
import locon.ManufacturerEntity

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
            ManufacturerEntity.findOrSaveWhere(name: "Schwarzkopf professional")
            ManufacturerEntity.findOrSaveWhere(name: "Screen")
        }
    }

    def destroy = {
    }
}
