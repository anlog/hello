package cc.ifnot.ax

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cc.ifnot.ax.databinding.FragmentMyBinding

class MyFragment(mCallback: MainActivity.MyFragmentLifecycleCallbacks?) : Fragment() {

    constructor() : this(null)

    private val mCallback = mCallback

    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!

    private val fm get() = fragmentManager!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        mCallback?.onFragmentViewCreated(fragmentManager!!, this, binding.root, savedInstanceState)
        binding.root.setBackgroundColor(Color.DKGRAY)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCallback?.onFragmentViewDestroyed(fm, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCallback?.onFragmentViewDestroyed(fm, this)
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mCallback?.onFragmentDestroyed(fm, this)
    }
}
