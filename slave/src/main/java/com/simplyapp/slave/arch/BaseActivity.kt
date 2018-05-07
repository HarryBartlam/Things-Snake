package com.simplyapp.slave.arch

import com.simplyapp.control.arch.AbsBaseActivity
import com.simplyapp.control.arch.BaseMvp

abstract class BaseActivity<T : BaseMvp.Presenter> : AbsBaseActivity<T>()
