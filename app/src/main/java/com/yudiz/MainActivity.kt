package com.yudiz

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private val modelLink = "https://github.com/yudiz-solutions/runtime_ar_android/raw/master/model/model.gltf"
    private lateinit var renderable: ModelRenderable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        arFragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            arFragment.onUpdate(frameTime)
        }

        // Build renderable from the link
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(
                        this,
                        Uri.parse(modelLink),
                        RenderableSource.SourceType.GLTF2).build())
                .setRegistryId(modelLink)
                .build()
                .thenAccept { renderable = it }
                .exceptionally {
                    Toast.makeText(this@MainActivity, "Error in fetching $modelLink", Toast.LENGTH_SHORT).show()
                    return@exceptionally null
                }

        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->

            // Create the Anchor.
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)

            // Create a transformable node and add it to the anchor.
            val node = TransformableNode(arFragment.transformationSystem)
            node.setParent(anchorNode)
            node.renderable = renderable
            node.select()
        }

    }
}
