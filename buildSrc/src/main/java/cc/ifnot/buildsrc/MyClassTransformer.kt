package cc.ifnot.buildsrc

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.google.auto.service.AutoService
import org.objectweb.asm.tree.ClassNode

/**
 * author: dp
 * created on: 2020/7/25 3:53 PM
 * description:
 */

@AutoService(ClassTransformer::class)
class MyClassTransformer : ClassTransformer {

    override fun onPostTransform(context: TransformContext) {
        super.onPostTransform(context)
        println("onPostTransform: $context")
    }

    override fun onPreTransform(context: TransformContext) {
        super.onPreTransform(context)
        println("onPreTransform: $context")
    }

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        println("transform: ${klass.name}")
        return super.transform(context, klass)
    }
}