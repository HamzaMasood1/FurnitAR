package com.razi.furnitar.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
//import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Sun;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.FixedHeightViewSizer;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;
import com.razi.furnitar.Adapters.UltraPagerAdapter;
import com.razi.furnitar.R;
import com.razi.furnitar.InternetConnectivity;
import com.razi.furnitar.Utils.UserPreference;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;

import java.util.ArrayList;
import java.util.List;

public class ARactivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    private static int pos = 0;
    private HitResult hit;
    private Session session;
    private static GoogleApiClient mGoogleApiClient;
    FirebaseAuth gAuth;
    FirebaseAuth.AuthStateListener aL;
    private static final String TAG = "ARactivity";
    private static final double MIN_OPENGL_VERSION = 3.0;
    InternetConnectivity it;
    private ArFragment arFragment;

    private UltraViewPager ultraViewPager;
    private UltraPagerAdapter adapter;
    private ModelRenderable model = null, line = null, marker_model = null, andyRenderable = null, cube = null, camera_model= null;
    private ViewRenderable distanceCardViewRenderable = null;

    private Spinner indicatorStyle;
    private Spinner indicatorGravityHor;
    private Spinner indicatorGravityVer;

    private int gravity_hor;
    private int gravity_ver;
    private UltraViewPager.Orientation gravity_indicator;

    ArrayList<AnchorNode> anchorNodes = new ArrayList<>();
    private float fl_measurement = 0.05f;
    private Anchor anchor1 = null, anchor2 = null, anchor3 = null, anchor4 = null, anchor5 = null;
    private AnchorNode firstAnchorNode = null, secondAnchorNode = null, thirdAnchorNode = null, fourthAnchorNode = null, fifthAnchorNode = null;
    private AnchorNode firstNormal = null, secondNormal = null, thirdNormal = null, fourthNormal = null;
    private SeekBar sk_height_control;
    private float upDistance = 0.5f;
    Button signout;

    private Integer numberOfAnchors = 0;

    private AnchorNode currentSelectedAnchorNode = null;

    private Node nodeForLine;
    private float width = 0, length = 0, height = 0;
    private ArrayList<Integer> items = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onDestroy() {
        unregisterReceiver(it);
        super.onDestroy();
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     * <p>
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     * <p>
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            finish();
        }

//        items.add(R.mipmap.chair1);
//        items.add(R.mipmap.chair2);
//        items.add(R.mipmap.chair3);
//        items.add(R.mipmap.chair4);
//        items.add(R.mipmap.chair5);

        setContentView(R.layout.activity_aractivity);

        signout = findViewById(R.id.signout);
        signout.setOnClickListener(v -> {
            UserPreference.getInstance().clearAll();
            startActivity(new Intent(ARactivity.this, LoginActivity.class));
        });

        int style = 5;
        ultraViewPager = (UltraViewPager) findViewById(R.id.ultra_viewpager);
        ultraViewPager.setVisibility(View.GONE);

        switch (style) {
            case 1:
                ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
                adapter = new UltraPagerAdapter(false, items);
                ultraViewPager.setAdapter(adapter);
                ultraViewPager.setInfiniteRatio(100);
                gravity_indicator = UltraViewPager.Orientation.HORIZONTAL;
                break;
            case 2:
                ultraViewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.VERTICAL);
                adapter = new UltraPagerAdapter(false, items);
                ultraViewPager.setAdapter(adapter);
                gravity_indicator = UltraViewPager.Orientation.VERTICAL;
                break;
            case 3:
            case 5:
            case 6:
                ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
                adapter = new UltraPagerAdapter(true, items);
                ultraViewPager.setAdapter(adapter);
                ultraViewPager.setMultiScreen(0.6f);
                ultraViewPager.setItemRatio(1.0f);
                ultraViewPager.setRatio(2.0f);
                ultraViewPager.setMaxHeight(800);
                ultraViewPager.setAutoMeasureHeight(true);
                gravity_indicator = UltraViewPager.Orientation.HORIZONTAL;
                if (style == 5) {
                    ultraViewPager.setPageTransformer(false, new UltraScaleTransformer());
                }
                if (style == 6) {
                    ultraViewPager.setPageTransformer(false, new UltraDepthScaleTransformer());
                }
                break;
            case 4:
                ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.VERTICAL);
                ultraViewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                adapter = new UltraPagerAdapter(true, items);
                ultraViewPager.setAdapter(adapter);
                ultraViewPager.setMultiScreen(1.0f);
                ultraViewPager.setAutoMeasureHeight(true);
                gravity_indicator = UltraViewPager.Orientation.VERTICAL;
                break;
        }

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        it = new InternetConnectivity();
        registerReceiver(it, in);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> Log.i("OK", "NOT OK"))
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        initRenderable();

//        arFragment.setOnTapArPlaneListener(
//                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
//
////                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
////                        return;
////                    }
//
//                    if (andyRenderable == null) {
//                        return;
//                    }
//
//                    hit = hitResult;
//
//                    Anchor anchor = hitResult.createAnchor();
//                    AnchorNode anchorNode = new AnchorNode(anchor);
//
//                    if (anchor3 != null) {
//                        removeModel();
//                        emptyAnchors();
//                        ultraViewPager.setVisibility(View.GONE);
//                        anchorNode.setRenderable(model);
//                        addAnchorNode(anchorNode);
//
//
////                        if(plane.getType() == Plane.Type.VERTICAL) {
////                            Vector3 anchorUp = anchorNode.getUp();
////                            andy.setLookDirection(Vector3.up(), anchorUp);
////                        }
////                            Vector3 anchorUp = anchorNode.getUp();
////                            andy.setLookDirection(Vector3.up(), anchorUp);
//
//
//                    }
//                    if (anchor2 != null && anchor3 == null) {
//                        anchor3 = anchor;
//                        thirdAnchorNode = new AnchorNode(anchor3);
//
//                        anchorNode.setRenderable(model);
//                        addAnchorNode(anchorNode);
//                        drawLine(secondAnchorNode, anchorNode);
//
//                        Vector3 point1, point2, point3, point4;
//                        point1 = firstAnchorNode.getWorldPosition();
//                        point2 = secondAnchorNode.getWorldPosition();
//                        point3 = thirdAnchorNode.getWorldPosition();
//
//                        Vector3 difference12 = Vector3.subtract(point1, point2);
//
//                        point4 = Vector3.add(point3, difference12);
//
//                        fourthAnchorNode = new AnchorNode();
//                        fourthAnchorNode.setParent(arFragment.getArSceneView().getScene());
//                        fourthAnchorNode.setLocalPosition(point4);
//                        fourthAnchorNode.setRenderable(model);
////                        addAnchorNode(fourthAnchorNode);
//                        drawLine(fourthAnchorNode, anchorNode);
//                        drawLine(firstAnchorNode, fourthAnchorNode);
//
////                        switch (pos) {
////                            case 0:
////                                createModelWithDimensions(0.6f, 0.7f, 0.6f);
////                                break;
////                            case 1:
////                                createModelWithDimensions(0.7f, 0.8f, 0.7f);
////                                break;
////                            case 2:
////                                createModelWithDimensions(0.8f, 0.9f, 0.8f);
////                                break;
////                            case 3:
////                                createModelWithDimensions(0.9f, 1.0f, 0.9f);
////                                break;
////                            case 4:
////                                createModelWithDimensions(0.38f, 0.81f, 0.59f);
////                                break;
////                        }
//
//                        ultraViewPager.setVisibility(View.VISIBLE);
//
//                    }
//                    if (anchor1 != null && anchor2 == null) {
//                        anchor2 = anchor;
//                        secondAnchorNode = new AnchorNode(anchor2);
//
//                        anchorNode.setRenderable(model);
//                        addAnchorNode(anchorNode);
//                        drawLine(firstAnchorNode, anchorNode);
//
//                    }
//                    if (anchor1 == null) {
//                        anchor1 = anchor;
//                        firstAnchorNode = new AnchorNode(anchor1);
//                        anchorNode.setRenderable(model);
//                        addAnchorNode(anchorNode);
//                    }
//
//                });

        arFragment.getArSceneView().getPlaneRenderer().setEnabled(false);
        arFragment.getArSceneView().getScene().addOnPeekTouchListener(this::handleOnTouch);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        ultraViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                Log.d("Selected Position", String.valueOf(i));
                pos = i;
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                Log.d("Selected Position", String.valueOf(i));
                    switch (items.get(pos)) {
                        case R.mipmap.chair1:
                            removeAnchorNode(fifthAnchorNode);
                            createModelWithDimensions(0.68f, 0.9f, 0.68f);
                            fifthAnchorNode.setLocalScale(new Vector3(6.8f, 9f, 6.8f));
                            break;
                        case R.mipmap.chair2:
                            removeAnchorNode(fifthAnchorNode);
                            createModelWithDimensions(0.68f, 0.94f, 0.68f);
                            fifthAnchorNode.setLocalScale(new Vector3(6.8f, 9.4f, 6.8f));
                            break;
                        case R.mipmap.chair3:
                            removeAnchorNode(fifthAnchorNode);
                            createModelWithDimensions(0.69f, 0.85f, 0.69f);
                            fifthAnchorNode.setLocalScale(new Vector3(6.9f, 8.5f, 6.9f));
                            break;
                        case R.mipmap.chair4:
                            removeAnchorNode(fifthAnchorNode);
                            createModelWithDimensions(0.8f, 1.1f, 0.8f);
                            fifthAnchorNode.setLocalScale(new Vector3(4.5f, 8f, 4.5f));
                            break;
                        case R.mipmap.chair5:
                            removeAnchorNode(fifthAnchorNode);
                            createModelWithDimensions(0.38f, 8.1f, 0.38f);
                            fifthAnchorNode.setLocalScale(new Vector3(3.8f, 8.1f, 3.8f));
                            break;
                    }
                }
        });

    }

    @SuppressLint("Assert")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleOnTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {

        arFragment.onPeekTouch(hitTestResult, motionEvent);

        if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
            return;
        }

        // Check for touching a Sceneform node
        if (hitTestResult.getNode() != null) {
            Log.d(TAG,"handleOnTouch hitTestResult.getNode() != null");
            //Toast.makeText(LineViewMainActivity.this, "hitTestResult is not null: ", Toast.LENGTH_SHORT).show();
            Node hitNode = hitTestResult.getNode();

            if (hitNode.getRenderable() == model) {
                //Toast.makeText(LineViewMainActivity.this, "We've hit Andy!!", Toast.LENGTH_SHORT).show();
                //First make the current (soon to be not current) selected node not highlighted
                if (currentSelectedAnchorNode != null) {
                    currentSelectedAnchorNode.setRenderable(model);
                }
                //Now highlight the new current selected node
                ModelRenderable highlightedAndyRenderable = model.makeCopy();
                highlightedAndyRenderable.getMaterial().setFloat3("baseColorTint", new Color(android.graphics.Color.rgb(255,0,0)));
                hitNode.setRenderable(highlightedAndyRenderable);
                currentSelectedAnchorNode = (AnchorNode) hitNode;
            }
            return;
        } else {
            // Place the anchor 1m in front of the camera. Make sure we are not at maximum anchor first.
            Log.d(TAG,"adding Andy in fornt of camera");

            Frame frame = arFragment.getArSceneView().getArFrame();

            if (frame == null) {
                return;
            }

            Session session = arFragment.getArSceneView().getSession();
            assert session != null;
            Anchor newMarkAnchor = session.createAnchor(
                    frame.getCamera().getPose()
                            .compose(Pose.makeTranslation(0, 0, -2f))
                            .extractTranslation());

            AnchorNode addedAnchorNode = new AnchorNode(newMarkAnchor);

            if (anchor5 != null) {
                removeModel();
                emptyAnchors();
                numberOfAnchors = 0;
                width = 0;
                length = 0;
                height = 0;
                items.clear();
                adapter = new UltraPagerAdapter(true, items);
                ultraViewPager.setAdapter(adapter);
                ultraViewPager.setVisibility(View.GONE);
            }

            if (anchor4 != null) {

//                removeModel();
//                emptyAnchors();
//                numberOfAnchors = 0;
//                ultraViewPager.setVisibility(View.GONE);

                anchor5 = newMarkAnchor;

                addedAnchorNode.setAnchor(null);

                firstNormal = new AnchorNode();
                secondNormal = new AnchorNode();
                thirdNormal = new AnchorNode();
                fourthNormal = new AnchorNode();

                Vector3 point1, point2, point3, point4, point5;

                point1 = firstAnchorNode.getWorldPosition();
                point2 = secondAnchorNode.getWorldPosition();
                point3 = thirdAnchorNode.getWorldPosition();
                point4 = fourthAnchorNode.getWorldPosition();

                point5 = addedAnchorNode.getWorldPosition();
                Vector3 difference = Vector3.subtract(point5, point4);
                height = difference.length();

                point5.x = point4.x;
                point5.z = point4.z;

                fourthNormal.setWorldPosition(point5);
                fourthNormal.setParent(arFragment.getArSceneView().getScene());
                drawLine(fourthAnchorNode, fourthNormal);


                point5 = addedAnchorNode.getWorldPosition();
                point5.x = point1.x;
                point5.z = point1.z;
//                point5.y = point1.y + point5.y- point4.y;
                firstNormal.setWorldPosition(point5);
                firstNormal.setParent(arFragment.getArSceneView().getScene());
                drawLine(firstAnchorNode, firstNormal);

                point5 = addedAnchorNode.getWorldPosition();
                point5.x = point2.x;
                point5.z = point2.z;
//                point5.y = point2.y + point5.y - point4.y;
                secondNormal.setWorldPosition(point5);
                secondNormal.setParent(arFragment.getArSceneView().getScene());
                drawLine(secondAnchorNode, secondNormal);

                point5 = addedAnchorNode.getWorldPosition();
                point5.x = point3.x;
                point5.z = point3.z;
//                point5.y = point3.y + point5.y - point4.y;
                thirdNormal.setWorldPosition(point5);
                thirdNormal.setParent(arFragment.getArSceneView().getScene());
                drawLine(thirdAnchorNode, thirdNormal);

                drawLine(firstNormal, secondNormal);
                drawLine(secondNormal, thirdNormal);
                drawLine(thirdNormal, fourthNormal);
                drawLine(fourthNormal, firstNormal);

                if (height > 1.1) {
                    if (width > 0.8) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair4);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair5);
                            adapter.updateArray(items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
                    else if (width < 0.8 && width > 0.69) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
                    else if (width < 0.69 && width > 0.68) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
                    else if (width < 0.68 && width > 0.38) {
                        if (length > 0.8) {
//                            items.add(R.mipmap.chair4);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
//                            items.add(R.mipmap.chair4);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
//                            items.add(R.mipmap.chair4);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter
                            );
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
//                    else if (width < 0.45 && width > 0.38) {
//                        if (length < 0.38) {
//                            items.clear();
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.GONE);
//                        } else {
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                    }
                    else {
                        items.clear();
                        adapter = new UltraPagerAdapter(true, items);
                        ultraViewPager.setAdapter(adapter);
                        ultraViewPager.setVisibility(View.GONE);
                    }
                }
                else if (height < 1.1 && height > 0.94) {
                    if (width > 0.8) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
                    else if (width < 0.8 && width > 0.69) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
                    else if (width < 0.69 && width > 0.68) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair2);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
//                    else if (width < 0.5 && width > 0.45) {
//                        if (length > 0.6) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.6 && length > 0.55) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.55 && length > 0.5) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.45 && length > 0.38) {
//                            items.add(R.mipmap.chair5);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.38) {
//                            items.clear();
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.GONE);
//                        }
//                    }
                    else if (width < 0.68 && width > 0.38) {
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        } else {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }

                    }
                    else {
                        items.clear();
                        adapter = new UltraPagerAdapter(true, items);
                        ultraViewPager.setAdapter(adapter);
                        ultraViewPager.setVisibility(View.GONE);
                    }
                }
                else if (height < 0.94 && height > 0.9 ) {
                    if (width > 0.8) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
//                            items.clear();
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
                    else if (width < 0.8 && width > 0.69) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
//                            items.clear();
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
                    else if (width < 0.69 && width > 0.68) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
//                            items.clear();
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
//                    else if (width < 0.5 && width > 0.45) {
//                        if (length > 0.6) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.6 && length > 0.55) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.55 && length > 0.5) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.45 && length > 0.38) {
//                            items.clear();
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.GONE);
//                        }
//                        if (length < 0.38) {
//                            items.clear();
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.GONE);
//                        }
//                    }
                    else if (width < 0.68 && width > 0.38) {
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        } else {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }

                    }
                    else {
                        items.clear();
                        adapter = new UltraPagerAdapter(true, items);
                        ultraViewPager.setAdapter(adapter);
                        ultraViewPager.setVisibility(View.GONE);
                    }
                }
                else if (height < 0.9 && height > 0.85 ) {
                    if (width > 0.8) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
//                            items.clear();
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
                    else if (width < 0.8 && width > 0.69) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair3);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
//                            items.clear();
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
                    else if (width < 0.69 && width > 0.68) {
                        if (length > 0.8) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.8 && length > 0.69) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
                        if (length < 0.69 && length > 0.68) {
                            items.add(R.mipmap.chair1);
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
                        if (length < 0.68 && length > 0.38) {
//                            items.clear();
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        }
                    }
//                    else if (width < 0.5 && width > 0.45) {
//                        if (length > 0.6) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.6 && length > 0.55) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.55 && length > 0.5) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.5 && length > 0.45) {
//                            items.add(R.mipmap.chair4);
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.VISIBLE);
//                        }
//                        if (length < 0.45 && length > 0.38) {
//                            items.clear();
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.GONE);
//                        }
//                        if (length < 0.38) {
//                            items.clear();
//                            adapter = new UltraPagerAdapter(true, items);
//                            ultraViewPager.setAdapter(adapter);
//                            ultraViewPager.setVisibility(View.GONE);
//                        }
//                    }
                    else if (width < 0.68 && width > 0.38) {
                        if (length < 0.38) {
                            items.clear();
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.GONE);
                        } else {
                            items.add(R.mipmap.chair5);
                            adapter = new UltraPagerAdapter(true, items);
                            ultraViewPager.setAdapter(adapter);
                            ultraViewPager.setVisibility(View.VISIBLE);
                        }

                    }
                    else {
                        items.clear();
                        adapter = new UltraPagerAdapter(true, items);
                        ultraViewPager.setAdapter(adapter);
                        ultraViewPager.setVisibility(View.GONE);
                    }
                }
                else if (height < 0.85 && height > 0.81 ) {
                    if (width > 0.38 && length > 0.38) {
                        items.add(R.mipmap.chair5);
                        adapter = new UltraPagerAdapter(true, items);
                        ultraViewPager.setAdapter(adapter);
                        ultraViewPager.setVisibility(View.GONE);
                    } else {
                        items.clear();
                        adapter = new UltraPagerAdapter(true, items);
                        ultraViewPager.setAdapter(adapter);
                        ultraViewPager.setVisibility(View.GONE);
                    }
                }
                else {
                    items.clear();
                    adapter = new UltraPagerAdapter(true, items);
                    ultraViewPager.setAdapter(adapter);
                    ultraViewPager.setVisibility(View.GONE);
                }

//                switch (items.get(pos)) {
//                    case R.mipmap.chair1:
//                        createModelWithDimensions(0.5f, 0.7f, 0.5f);
//                        fifthAnchorNode.setLocalScale(new Vector3(5f, 7f, 5f));
//                        break;
//                    case R.mipmap.chair2:
//                        createModelWithDimensions(0.55f, 0.8f, 0.55f);
//                        fifthAnchorNode.setLocalScale(new Vector3(5.5f, 8f, 5.5f));
//                        break;
//                    case R.mipmap.chair3:
//                        createModelWithDimensions(0.6f, 0.9f, 0.6f);
//                        fifthAnchorNode.setLocalScale(new Vector3(6f, 9f, 6f));
//                        break;
//                    case R.mipmap.chair4:
//                        createModelWithDimensions(0.45f, 0.8f, 0.45f);
//                        fifthAnchorNode.setLocalScale(new Vector3(4.5f, 8f, 4.5f));
//                        break;
//                    case R.mipmap.chair5:
//                        createModelWithDimensions(0.38f, 8.1f, 0.38f);
//                        fifthAnchorNode.setLocalScale(new Vector3(3.8f, 8.1f, 3.8f));
//                        break;
//                }

            }

            if (anchor3 != null && anchor4 == null) {
                anchor4 = newMarkAnchor;
                Vector3 point4 = addedAnchorNode.getWorldPosition();
                addedAnchorNode.setWorldPosition(point4);
                addedAnchorNode.setRenderable(camera_model);
                currentSelectedAnchorNode = addedAnchorNode;
                fourthAnchorNode = addedAnchorNode;
                drawLine(thirdAnchorNode, addedAnchorNode);
                addAnchorNode(addedAnchorNode);
                drawLine(fourthAnchorNode, firstAnchorNode);

//                addedAnchorNode.setAnchor(null);
//
//                firstNormal = new AnchorNode();
//                secondNormal = new AnchorNode();
//                thirdNormal = new AnchorNode();
//                fourthNormal = new AnchorNode();
//
//                Vector3 point1, point2, point3, point4, point5;
//
//                point5 = addedAnchorNode.getWorldPosition();
//
//                point1 = firstAnchorNode.getWorldPosition();
//                point2 = secondAnchorNode.getWorldPosition();
//                point3 = thirdAnchorNode.getWorldPosition();
//                point4 = fourthAnchorNode.getWorldPosition();
//
//                point5.x = point3.x;
//                point5.z = point3.z;
//
//                thirdNormal.setWorldPosition(point5);
//                thirdNormal.setParent(arFragment.getArSceneView().getScene());
//                drawLine(thirdAnchorNode, thirdNormal);
//
//
//                point5 = addedAnchorNode.getWorldPosition();
//                point5.x = point1.x;
//                point5.z = point1.z;
//                point5.y = point1.y + point5.y- point3.y;
//                firstNormal.setWorldPosition(point5);
//                firstNormal.setParent(arFragment.getArSceneView().getScene());
//                drawLine(firstAnchorNode, firstNormal);
//
//                point5 = addedAnchorNode.getWorldPosition();
//                point5.x = point2.x;
//                point5.z = point2.z;
//                point5.y = point2.y + point5.y - point3.y;
//                secondNormal.setWorldPosition(point5);
//                secondNormal.setParent(arFragment.getArSceneView().getScene());
//                drawLine(secondAnchorNode, secondNormal);
//
//                point5 = addedAnchorNode.getWorldPosition();
//                point5.x = point4.x;
//                point5.z = point4.z;
//                point5.y = point3.y + point5.y - point3.y;
//                fourthNormal.setWorldPosition(point5);
//                fourthNormal.setParent(arFragment.getArSceneView().getScene());
//                drawLine(fourthAnchorNode, fourthNormal);
//
//                drawLine(firstNormal, secondNormal);
//                drawLine(secondNormal, thirdNormal);
//                drawLine(thirdNormal, fourthNormal);
//                drawLine(fourthNormal, firstNormal);
//
//                ultraViewPager.setVisibility(View.VISIBLE);
//                switch (pos) {
//                    case 0:
//                        createModelWithDimensions(0.5f, 0.7f, 0.5f);
//                        fifthAnchorNode.setLocalScale(new Vector3(5f, 7f, 5f));
//                        break;
//                    case 1:
//                        createModelWithDimensions(0.55f, 0.8f, 0.55f);
//                        fifthAnchorNode.setLocalScale(new Vector3(5.5f, 8f, 5.5f));
//                        break;
//                    case 2:
//                        createModelWithDimensions(0.6f, 0.9f, 0.6f);
//                        fifthAnchorNode.setLocalScale(new Vector3(6f, 9f, 6f));
//                        break;
//                    case 3:
//                        createModelWithDimensions(0.45f, 1.0f, 0.45f);
//                        fifthAnchorNode.setLocalScale(new Vector3(4.5f, 10f, 4.5f));
//                        break;
//                    case 4:
//                        createModelWithDimensions(0.38f, 0.81f, 0.38f);
//                        fifthAnchorNode.setLocalScale(new Vector3(3.8f, 8.1f, 3.8f));
//                        break;
//                }

            }

            if (anchor2 != null && anchor3 == null) {
                anchor3 = newMarkAnchor;
                Vector3 point3 = addedAnchorNode.getWorldPosition();
                Vector3 point2 = secondAnchorNode.getWorldPosition();
                Vector3 difference = Vector3.subtract(point3, point2);
                length = difference.length();
//                point3.y = 0;
                addedAnchorNode.setWorldPosition(point3);
                addedAnchorNode.setRenderable(camera_model);
                addAnchorNode(addedAnchorNode);
                thirdAnchorNode = addedAnchorNode;
                currentSelectedAnchorNode = thirdAnchorNode;
                drawLine(secondAnchorNode, addedAnchorNode);

//                Vector3 point1 = firstAnchorNode.getWorldPosition();
//                Vector3 point2 = secondAnchorNode.getWorldPosition();
//                Vector3 difference21 = Vector3.subtract(point1, point2);
//                Vector3 difference23 = Vector3.subtract(point3, point2);
//                Vector3 point4 = Vector3.add(point3, difference21);
//
//                fourthAnchorNode = new AnchorNode();
//                fourthAnchorNode.setWorldPosition(point4);
//                fourthAnchorNode.setRenderable(camera_model);
//                fourthAnchorNode.setParent(arFragment.getArSceneView().getScene());
//
//
//                drawLine(addedAnchorNode, fourthAnchorNode);
//                drawLine(fourthAnchorNode, firstAnchorNode);
            }

            if (anchor1 != null && anchor2 == null) {
                anchor2 = newMarkAnchor;
                Vector3 point2 = addedAnchorNode.getWorldPosition();
                Vector3 point1 = firstAnchorNode.getWorldPosition();
                Vector3 difference = Vector3.subtract(point2, point1);
                width = difference.length();
//                point2.y = 0;
//                addedAnchorNode.setWorldPosition(point2);
//                addedAnchorNode.setRenderable(camera_model);
//                addAnchorNode(addedAnchorNode);
//                drawLine(firstAnchorNode, addedAnchorNode);
//                secondAnchorNode = addedAnchorNode;
                secondAnchorNode = new AnchorNode();
                secondAnchorNode.setWorldPosition(point2);
                secondAnchorNode.setRenderable(camera_model);
                secondAnchorNode.setParent(arFragment.getArSceneView().getScene());
                currentSelectedAnchorNode = secondAnchorNode;
                drawLine(firstAnchorNode, secondAnchorNode);
            }

            if (anchor1 == null) {
                anchor1 = newMarkAnchor;
                Vector3 point1 = addedAnchorNode.getWorldPosition();
//                point1.y = 0;
//                addedAnchorNode.setWorldPosition(point1);
//                addedAnchorNode.setRenderable(camera_model);
//                addAnchorNode(addedAnchorNode);
//                firstAnchorNode = addedAnchorNode;
                firstAnchorNode = new AnchorNode();
                firstAnchorNode.setWorldPosition(point1);
                firstAnchorNode.setRenderable(camera_model);
                firstAnchorNode.setParent(arFragment.getArSceneView().getScene());
                currentSelectedAnchorNode = firstAnchorNode;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdateFrame(FrameTime frameTime) {

        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame, just return.
        if (frame == null) {
            return;
        }

        //Making sure ARCore is tracking some feature points, makes the augmentation little stable.
        if(frame.getCamera().getTrackingState()== TrackingState.TRACKING) {
            Pose pos = frame.getCamera().getPose().compose(Pose.makeTranslation(0, 0, -1f));
            Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pos);
            AnchorNode anchorNode = new AnchorNode(anchor);
//            anchorNode.setRenderable(marker_model);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
//            anchorNodes1.add(anchorNode);
//            int lastIndex = anchorNodes1.size() - 1;
//            AnchorNode currentNode, lastNode;
//            lastNode = anchorNodes1.get(lastIndex);


//            if (currentSelectedAnchorNode != null) {
//                drawLine(currentSelectedAnchorNode, anchorNode);
//            }


//            Node arrow = new Node();
//            arrow.setParent(anchorNode);
//            arrow.setRenderable(model);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createModel(AnchorNode anchorNode, float r) {
        {
            MaterialFactory.makeTransparentWithColor(this, new Color(255, 0, 0))
                    .thenAccept(
                            material -> {
//                                model = ShapeFactory.makeSphere(r, Vector3.zero(), material);
                                model = ShapeFactory.makeCylinder(r, 0.002f, Vector3.zero(), material);
                                model.setShadowCaster(false);
                                model.setShadowReceiver(false);
                            }
                    );

//            TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
//            andy.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));
//            andy.setParent(anchorNode);
//            andy.setRenderable(model);
//            andy.getScaleController().setEnabled(false);
//            andy.select();

            Node andy = new Node();
//            andy.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));
            andy.setParent(anchorNode);
            andy.setRenderable(model);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createModelWithDimensions(float v, float v1, float v2) {

        Vector3 point1, point3;
        point1 = firstAnchorNode.getWorldPosition();
        point3 = thirdAnchorNode.getWorldPosition();

        Vector3 point5 = Vector3.add(point1, point3).scaled(0.5f);
        fifthAnchorNode = new AnchorNode();
        fifthAnchorNode.setParent(arFragment.getArSceneView().getScene());
        fifthAnchorNode.setWorldPosition(point5);

//        MaterialFactory.makeTransparentWithColor(this, new Color(0, 255, 100))
//                .thenAccept(
//                        material -> {
//                            cube = ShapeFactory.makeCube(
//                                    new Vector3(v, v1, v2),
//                                    new Vector3(0,0,0), material);
//
//                        }
//                ).exceptionally(
//                    throwable -> {
//                        Toast toast =
//                                Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
//                        return null;
//                });

        TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
//        andy.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 15f));
        andy.setParent(fifthAnchorNode);
        andy.setRenderable(andyRenderable);

        andy.getScaleController().setEnabled(false);
        andy.select();

    }

    @Override
    public void onClick(View v) {
        ultraViewPager.getIndicator().build();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (ultraViewPager.getIndicator() == null) {
            ultraViewPager.initIndicator();
            ultraViewPager.getIndicator().setOrientation(gravity_indicator);
        }
        if (parent == indicatorStyle) {
            switch (position) {
                case 0:
                    ultraViewPager.disableIndicator();
                    break;
                case 1:
                    ultraViewPager.getIndicator().setFocusResId(0).setNormalResId(0);
                    ultraViewPager.getIndicator().setFocusColor(1).setNormalColor(1)
                            .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
                    break;
                case 2:
                    ultraViewPager.getIndicator().setFocusResId(R.mipmap.tm_biz_lifemaster_indicator_selected)
                            .setNormalResId(R.mipmap.tm_biz_lifemaster_indicator_normal);
                    break;
                case 3:
                    break;
            }
        }
        if (parent == indicatorGravityHor) {
            switch (position) {
                case 0:
                    gravity_hor = Gravity.LEFT;
                    break;
                case 1:
                    gravity_hor = Gravity.RIGHT;
                    break;
                case 2:
                    gravity_hor = Gravity.CENTER_HORIZONTAL;
                    break;
            }
            if (ultraViewPager.getIndicator() != null) {
                if (gravity_ver != 0) {
                    ultraViewPager.getIndicator().setGravity(gravity_hor | gravity_ver);
                } else {
                    ultraViewPager.getIndicator().setGravity(gravity_hor);
                }
            }
        }
        if (parent == indicatorGravityVer) {
            switch (position) {
                case 0:
                    gravity_ver = Gravity.TOP;
                    break;
                case 1:
                    gravity_ver = Gravity.BOTTOM;
                    break;
                case 2:
                    gravity_ver = Gravity.CENTER_VERTICAL;
                    break;
            }
            if (ultraViewPager.getIndicator() != null) {
                if (gravity_hor != 0) {
                    ultraViewPager.getIndicator().setGravity(gravity_hor | gravity_ver);
                } else {
                    ultraViewPager.getIndicator().setGravity(gravity_ver);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void removeModel() {
        {
            List<Node> children = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());
            for (Node node : children) {
                if (node instanceof AnchorNode) {
                    if (((AnchorNode) node).getAnchor() != null) {

                        ((AnchorNode) node).getAnchor().detach();
                    }
                }
                if (!(node instanceof Camera) && !(node instanceof Sun)) {
                    node.setParent(null);
                }
            }
        }
    }

    private void emptyAnchors() {

        anchor1 = null;
        anchor2 = null;
        anchor3 = null;
        anchor4 = null;
        anchor5 = null;
        for (AnchorNode n : anchorNodes) {
            arFragment.getArSceneView().getScene().removeChild(n);
            n.getAnchor().detach();
            n.setParent(null);
            n = null;
        }
    }

    private void removeAnchorNode(AnchorNode nodeToremove) {
        //Remove an anchor node
        if (nodeToremove != null) {
            arFragment.getArSceneView().getScene().removeChild(nodeToremove);
            anchorNodes.remove(nodeToremove);
            nodeToremove.setParent(null);
            nodeToremove = null;
        } else {
            Toast.makeText(this, "Delete - no node selected! Touch a node to select it.", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeLine(Node lineToRemove) {
        //remove the line
        Log.e(TAG, "removeLine");
        if (lineToRemove != null) {
            Log.e(TAG, "removeLine lineToRemove is not mull");
            arFragment.getArSceneView().getScene().removeChild(lineToRemove);
            lineToRemove.setParent(null);
            lineToRemove = null;
        }
    }

    private void addAnchorNode(AnchorNode nodeToAdd) {
        //Add an anchor node
        nodeToAdd.setParent(arFragment.getArSceneView().getScene());
        anchorNodes.add(nodeToAdd);
        numberOfAnchors++;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private AnchorNode moveRenderable(AnchorNode markAnchorNodeToMove, Pose newPoseToMoveTo) {
        //Move a renderable to a new pose
        if (markAnchorNodeToMove != null) {
            arFragment.getArSceneView().getScene().removeChild(markAnchorNodeToMove);
            anchorNodes.remove(markAnchorNodeToMove);
        } else {
            Log.d(TAG,"moveRenderable - markAnchorNode was null, the little £$%^...");
            return null;
        }
        Frame frame = arFragment.getArSceneView().getArFrame();
        Session session = arFragment.getArSceneView().getSession();
        Anchor markAnchor = session.createAnchor(newPoseToMoveTo.extractTranslation());
        AnchorNode newMarkAnchorNode = new AnchorNode(markAnchor);

        MaterialFactory.makeTransparentWithColor(this, new Color(0, 255, 244))
                .thenAccept(
                        material -> {
                            model = ShapeFactory.makeSphere(0.02f, Vector3.zero(), material);
                            model.setShadowCaster(false);
                            model.setShadowReceiver(false);
                        }
                );

        newMarkAnchorNode.setRenderable(model);
        newMarkAnchorNode.setParent(arFragment.getArSceneView().getScene());
        anchorNodes.add(newMarkAnchorNode);

        //Delete the line if it is drawn
        removeLine(nodeForLine);

        return newMarkAnchorNode;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void drawLine(AnchorNode node1, AnchorNode node2) {
        //Draw a line between two AnchorNodes (adapted from https://stackoverflow.com/a/52816504/334402)
        Log.d(TAG,"drawLine");

        Vector3 point1, point2;
        point1 = node1.getWorldPosition();
        point2 = node2.getWorldPosition();

        //First, find the vector extending between the two points and define a look rotation
        //in terms of this Vector.
        final Vector3 difference = Vector3.subtract(point1, point2);
        final Vector3 directionFromTopToBottom = difference.normalized();
        final Quaternion rotationFromAToB =
                Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
        MaterialFactory.makeTransparentWithColor(this, new Color(0, 255, 254))
                .thenAccept(
                        material -> {
                            /* Then, create a rectangular prism, using ShapeFactory.makeCube() and use the difference vector
                                   to extend to the necessary length.  */
                            Log.d(TAG,"drawLine insie .thenAccept");
                            line = ShapeFactory.makeCube(
                                    new Vector3(.005f, .005f, difference.length()),
                                    Vector3.zero(), material);

                            float distance = difference.length() * 100;
                            TextView textView = distanceCardViewRenderable.getView().findViewById(R.id.distanceCard1);
                            textView.setText(String.format("%.2f", distance) + "cm");

//                            Node centernode = new Node();
//                            centernode.setParent(arFragment.getArSceneView().getScene());
//                            centernode.setWorldPosition(Vector3.add(point1, point2).scaled(.5f));
//                            centernode.setRenderable(distanceCardViewRenderable);
//                            centernode.setWorldRotation(rotationFromAToB);
                            nodeForLine = new Node();
                            nodeForLine.setParent(node2);
                            nodeForLine.setRenderable(line);
                            nodeForLine.setWorldPosition(Vector3.add(point1, point2).scaled(.5f));
                            nodeForLine.setWorldRotation(rotationFromAToB);
                        }
                );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initRenderable() {

        MaterialFactory
                .makeTransparentWithColor(this, new Color(255, 0, 0))
                .thenAccept(
                        material -> {
                            model = ShapeFactory.makeCylinder(0.02f, 0.002f, Vector3.zero(), material);
                            model.setShadowCaster(false);
                            model.setShadowReceiver(false);
                        }
                )
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable
                .builder()
                .setSource(this, R.raw.cubito3)
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        MaterialFactory.makeTransparentWithColor(this, new Color(255, 0, 0))
                .thenAccept(
                        material -> {

                            camera_model = ShapeFactory.makeCylinder(0.01f, 0.002f, Vector3.zero(), material);
                            camera_model.setShadowCaster(false);
                            camera_model.setShadowReceiver(false);
                        }
                )
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ViewRenderable
                .builder()
                .setView(this, R.layout.distance_text_layout)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.BOTTOM)
                .setSizer(new FixedHeightViewSizer(0.12f))
                .build().thenAccept(
                        renderable-> {
                            distanceCardViewRenderable = renderable;
                            distanceCardViewRenderable.isShadowCaster();
                            distanceCardViewRenderable.isShadowReceiver();
                        }
                )
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        MaterialFactory.makeTransparentWithColor(this, new Color(255, 0, 0))
                .thenAccept(
                        material -> {
                            marker_model = ShapeFactory.makeSphere(0.01f, Vector3.zero(), material);
                            marker_model.setShadowCaster(false);
                            marker_model.setShadowReceiver(false);
                        }
                );
    }

}
