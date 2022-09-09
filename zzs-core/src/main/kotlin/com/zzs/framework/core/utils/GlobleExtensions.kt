package com.zzs.framework.core.utils

import com.zzs.framework.core.lang.TreeNode

fun <E : TreeNode> Collection<E>.toTreeList(): List<E> = TreeNode.toTreeList(this)
