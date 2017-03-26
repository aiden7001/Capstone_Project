/*
 *
 *  BlueZ - Bluetooth protocol stack for Linux
 *
 *  Copyright (C) 2012-2014  Intel Corporation. All rights reserved.
 *
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

#include <stdint.h>
#include <stdlib.h>
#include <alloca.h>
#include <byteswap.h>

#if __BYTE_ORDER == __LITTLE_ENDIAN
#define le16_to_cpu(val) (val)
#define le32_to_cpu(val) (val)
#define le64_to_cpu(val) (val)
#define cpu_to_le16(val) (val)
#define cpu_to_le32(val) (val)
#define cpu_to_le64(val) (val)
#define be16_to_cpu(val) bswap_16(val)
#define be32_to_cpu(val) bswap_32(val)
#define be64_to_cpu(val) bswap_64(val)
#define cpu_to_be16(val) bswap_16(val)
#define cpu_to_be32(val) bswap_32(val)
#define cpu_to_be64(val) bswap_64(val)
#elif __BYTE_ORDER == __BIG_ENDIAN
#define le16_to_cpu(val) bswap_16(val)
#define le32_to_cpu(val) bswap_32(val)
#define le64_to_cpu(val) bswap_64(val)
#define cpu_to_le16(val) bswap_16(val)
#define cpu_to_le32(val) bswap_32(val)
#define cpu_to_le64(val) bswap_64(val)
#define be16_to_cpu(val) (val)
#define be32_to_cpu(val) (val)
#define be64_to_cpu(val) (val)
#define cpu_to_be16(val) (val)
#define cpu_to_be32(val) (val)
#define cpu_to_be64(val) (val)
#else
#error "Unknown byte order"
#endif

#define get_unaligned(ptr)			\
({						\
	struct __attribute__((packed)) {	\
		typeof(*(ptr)) __v;		\
	} *__p = (typeof(__p)) (ptr);		\
	__p->__v;				\
})

#define put_unaligned(val, ptr)			\
do {						\
	struct __attribute__((packed)) {	\
		typeof(*(ptr)) __v;		\
	} *__p = (typeof(__p)) (ptr);		\
	__p->__v = (val);			\
} while (0)

#define PTR_TO_UINT(p) ((unsigned int) ((uintptr_t) (p)))
#define UINT_TO_PTR(u) ((void *) ((uintptr_t) (u)))

#define PTR_TO_INT(p) ((int) ((intptr_t) (p)))
#define INT_TO_PTR(u) ((void *) ((intptr_t) (u)))

#define new0(t, n) ((t*) calloc((n), sizeof(t)))
#define newa(t, n) ((t*) alloca(sizeof(t)*(n)))
#define malloc0(n) (calloc((n), 1))

typedef void (*util_debug_func_t)(const char *str, void *user_data);

void util_debug(util_debug_func_t function, void *user_data,
						const char *format, ...)
					__attribute__((format(printf, 3, 4)));

void util_hexdump(const char dir, const unsigned char *buf, size_t len,
				util_debug_func_t function, void *user_data);

static inline void bswap_128(const void *src, void *dst)
{
	const uint8_t *s = src;
	uint8_t *d = dst;
	int i;

	for (i = 0; i < 16; i++)
		d[15 - i] = s[i];
}

static inline uint16_t get_le16(const void *ptr)
{
	return le16_to_cpu(get_unaligned((const uint16_t *) ptr));
}

static inline uint16_t get_be16(const void *ptr)
{
	return be16_to_cpu(get_unaligned((const uint16_t *) ptr));
}

static inline uint32_t get_le32(const void *ptr)
{
	return le32_to_cpu(get_unaligned((const uint32_t *) ptr));
}

static inline uint32_t get_be32(const void *ptr)
{
	return be32_to_cpu(get_unaligned((const uint32_t *) ptr));
}

static inline uint64_t get_le64(const void *ptr)
{
	return le64_to_cpu(get_unaligned((const uint64_t *) ptr));
}

static inline uint64_t get_be64(const void *ptr)
{
	return be64_to_cpu(get_unaligned((const uint64_t *) ptr));
}

static inline void put_le16(uint16_t val, void *dst)
{
	put_unaligned(cpu_to_le16(val), (uint16_t *) dst);
}

static inline void put_be16(uint16_t val, const void *ptr)
{
	put_unaligned(cpu_to_be16(val), (uint16_t *) ptr);
}

static inline void put_le32(uint32_t val, void *dst)
{
	put_unaligned(cpu_to_le32(val), (uint32_t *) dst);
}

static inline void put_be32(uint32_t val, void *dst)
{
	put_unaligned(cpu_to_be32(val), (uint32_t *) dst);
}

static inline void put_le64(uint64_t val, void *dst)
{
	put_unaligned(cpu_to_le64(val), (uint64_t *) dst);
}

static inline void put_be64(uint64_t val, void *dst)
{
	put_unaligned(cpu_to_be64(val), (uint64_t *) dst);
}
