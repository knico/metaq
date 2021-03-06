/*
 * $Id: StringUtil.h 3 2011-08-19 02:25:45Z  $
 */
#ifndef LWPR_STRING_UTIL_H__
#define LWPR_STRING_UTIL_H__
#include "LWPRType.h"
#include "Buffer.h"
#include <stdio.h>
#include <stdarg.h>
#include <vector>

namespace LWPR
{
	class StringUtil
	{
	public:
		/**
		 * 去除前后空格、制表符、换行符
		 */
		static char* TrimAll(char *str);
		static char* TrimAll(char *buf, int num);

		/**
		 * 去除前后引号, 然后去除前后空格、制表符、换行符
		 */
		static char* TrimQuotationChar(char *str);

		/**
		 * 是否是空白字符（空格、制表符、换行符）
		 */
		static bool IsBlankChar(char ch);

		static std::string& ToUpper(std::string& str);

		static std::string& ToLower(std::string& str);

		/**
		 * 在一个字符缓冲区中查找字符ch首次出现的位置
		 */
		static int Find(const char* buf, int num, char ch);

		static void VForm(Buffer& buf, const char* format, va_list args);

		static void SplitString(const std::string& src, char delimiter, std::vector< std::string >& vs);

		/**
		 * 类型转化
		 */
		static std::string IntToStr(int num);

		static void StringLinesToVector(const char* str, std::vector<std::string>& vct);
	};
};

#endif // end of LWPR_STRING_UTIL_H__
