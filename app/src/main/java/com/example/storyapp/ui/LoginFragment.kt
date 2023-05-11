package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.airbnb.lottie.LottieAnimationView
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.ui.viewmodel.AuthViewModel
import com.example.storyapp.utils.Constanta.EMAIL
import com.example.storyapp.utils.Constanta.IS_LOGIN
import com.example.storyapp.utils.Constanta.NAME
import com.example.storyapp.utils.Constanta.TOKEN
import com.example.storyapp.utils.Constanta.USER_ID
import com.example.storyapp.utils.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var animView: LottieAnimationView
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var pref: Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = Preferences(requireActivity())
        animView = binding.logo
        animView.enableMergePathsForKitKatAndAbove(true)

        playAnimation()

        binding.btnLogin.setOnClickListener {
            if (binding.edtEmail.text?.length ?: 0 <= 0) {
                binding.edtEmail.error = getString(R.string.required_field)
                binding.edtEmail.requestFocus()
            } else if (binding.edtPassword.text?.length ?: 0 <= 0) {
                binding.edtPassword.error = getString(R.string.required_field)
                binding.edtPassword.requestFocus()
            } else if (binding.edtEmail.error?.length ?: 0 > 0) {
                binding.edtEmail.requestFocus()
            } else if (binding.edtPassword.error?.length ?: 0 > 0) {
                binding.edtPassword.requestFocus()
            } else {
                val email = binding.edtEmail.text.toString()
                val password = binding.edtPassword.text.toString()
                viewModel.getLogin(email, password)
            }
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.isLoading.observe(viewLifecycleOwner) {
                    showLoading(it)
                }
                viewModel.login.observe(viewLifecycleOwner) {
                    pref.apply {
                        setStringPreference(USER_ID, it.loginResult.userId)
                        setStringPreference(TOKEN, it.loginResult.token)
                        setStringPreference(NAME, it.loginResult.name)
                        setStringPreference(EMAIL, viewModel.tempEmail.value.toString())
                        setBooleanPreference(IS_LOGIN, true)
                    }
                    if (it.loginResult.token.isNotEmpty()) {
                        requireActivity().run {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        Toast.makeText(
                            activity,
                            resources.getString(R.string.welcome) + it.loginResult.name,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                viewModel.error.observe(viewLifecycleOwner) {
                    it?.let {
                        if (it.isNotEmpty()) {
                            Toast.makeText(
                                activity,
                                resources.getString(R.string.failed) + it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(
                    R.id.frame_container,
                    RegisterFragment(),
                    RegisterFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun playAnimation() {
        val welcome = ObjectAnimator.ofFloat(binding.label, View.ALPHA, 1f).setDuration(500)
        val appName = ObjectAnimator.ofFloat(binding.appName, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val register =
            ObjectAnimator.ofFloat(binding.registerContainer, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(welcome, appName, login, email, password, btnLogin, register)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.root.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}