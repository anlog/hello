package cc.ifnot.buildsrc

import com.android.build.gradle.api.BaseVariant
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.task.spi.VariantProcessor
import com.google.auto.service.AutoService
import org.gradle.api.Project

/**
 * author: dp
 * created on: 2020/7/25 4:19 PM
 * description:
 */

@AutoService(VariantProcessor::class)
class MyVariantProcessor(val project: Project) : VariantProcessor {
    init {
        println(project.name)
    }

    override fun process(variant: BaseVariant) {
        println("${variant.project.name} -> ${variant.name}")
    }
}