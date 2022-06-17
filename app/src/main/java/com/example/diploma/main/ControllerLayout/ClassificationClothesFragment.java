package com.example.diploma.main.ControllerLayout;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diploma.R;
import com.example.diploma.databinding.FragmentClassificationClothesBinding;
import com.example.diploma.ml.Model;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.Map;


public class ClassificationClothesFragment extends Fragment {

    private FragmentClassificationClothesBinding binding;
    private List<String> labels;
    private String name;

    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentClassificationClothesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(() ->
                NavHostFragment.findNavController(ClassificationClothesFragment.this)
                        .navigate(R.id.action_classificationClothesFragment_to_fragment_clothes), SPLASH_DISPLAY_LENGTH);
    }

    private void checkPhotoInML(@NonNull Bitmap bitmap) throws IOException {
        Model model = Model.newInstance(requireContext());
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        TensorImage image = new TensorImage(DataType.FLOAT32);
        image.load(bitmap);
        image = imageProcessor.process(image);

        Model.Outputs process = model.process(image);
        TensorBuffer outputFeature0 = process.getLocationAsTensorBuffer();
        showResult(outputFeature0);
        model.close();
    }

    private void showResult(@NonNull TensorBuffer buffer) {
        try {
            labels = FileUtil.loadLabels(requireContext(), "data.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Float> labeledProbability = new TensorLabel(labels, buffer).getMapWithFloatValue();
        float maxValueInMap = (Collections.max(labeledProbability.values()));

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet())
            if (entry.getValue() == maxValueInMap)
                name = entry.getKey();
    }

    @NonNull
    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
}