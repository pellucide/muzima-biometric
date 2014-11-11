#ifndef AN_FP_IMAGE_ASCII_BINARY_RECORD_H_INCLUDED
#define AN_FP_IMAGE_ASCII_BINARY_RECORD_H_INCLUDED

#include <Biometrics/Standards/ANImageAsciiBinaryRecord.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(ANFPImageAsciiBinaryRecord, ANImageAsciiBinaryRecord)

#define AN_FP_IMAGE_ASCII_BINARY_RECORD_FIELD_IMP  3
#define AN_FP_IMAGE_ASCII_BINARY_RECORD_FIELD_FGP 13
#define AN_FP_IMAGE_ASCII_BINARY_RECORD_FIELD_PD  14
#define AN_FP_IMAGE_ASCII_BINARY_RECORD_FIELD_PPC 15

#define AN_FP_IMAGE_ASCII_BINARY_RECORD_MAX_POSITION_COUNT        6
#define AN_FP_IMAGE_ASCII_BINARY_RECORD_MAX_PRINT_POSITION_COUNT 12

typedef enum ANFMajorCase_
{
	anfmcNA = 0,
	anfmcEji = 1,
	anfmcTip = 2,
	anfmcFV1 = 3,
	anfmcFV2 = 4,
	anfmcFV3 = 5,
	anfmcFV4 = 6,
	anfmcPrx = 7,
	anfmcDst = 8,
	anfmcMed = 9
} ANFMajorCase;

N_DECLARE_TYPE(ANFMajorCase)

struct ANFPositionDescriptor_
{
	BdifFPPosition Position;
	ANFMajorCase Portion;
};
#ifndef AN_FP_IMAGE_ASCII_BINARY_RECORD_HPP_INCLUDED
typedef struct ANFPositionDescriptor_ ANFPositionDescriptor;
#endif

N_DECLARE_TYPE(ANFPositionDescriptor)

struct ANFPrintPosition_
{
	ANFMajorCase FingerView;
	ANFMajorCase Segment;
	NInt Left;
	NInt Right;
	NInt Top;
	NInt Bottom;
};
#ifndef AN_FP_IMAGE_ASCII_BINARY_RECORD_HPP_INCLUDED
typedef struct ANFPrintPosition_ ANFPrintPosition;
#endif

N_DECLARE_TYPE(ANFPrintPosition)

struct ANFPQualityMetric_
{
	BdifFPPosition Position;
	NByte Score;
	NUShort AlgorithmVendorId;
	NUShort AlgorithmProductId;
};
#ifndef AN_FP_IMAGE_ASCII_BINARY_RECORD_HPP_INCLUDED
typedef struct ANFPQualityMetric_ ANFPQualityMetric;
#endif

N_DECLARE_TYPE(ANFPQualityMetric)

NResult N_API ANFPImageAsciiBinaryRecordGetPositionCount(HANFPImageAsciiBinaryRecord hRecord, NInt * pValue);
NResult N_API ANFPImageAsciiBinaryRecordGetPosition(HANFPImageAsciiBinaryRecord hRecord, NInt index, BdifFPPosition * pValue);
NResult N_API ANFPImageAsciiBinaryRecordGetPositionsEx(HANFPImageAsciiBinaryRecord hRecord, BdifFPPosition * arValue, NInt valueLength);
NResult N_API ANFPImageAsciiBinaryRecordSetPosition(HANFPImageAsciiBinaryRecord hRecord, NInt index, BdifFPPosition value);
NResult N_API ANFPImageAsciiBinaryRecordAddPosition(HANFPImageAsciiBinaryRecord hRecord, BdifFPPosition value);
NResult N_API ANFPImageAsciiBinaryRecordInsertPosition(HANFPImageAsciiBinaryRecord hRecord, NInt index, BdifFPPosition value);
NResult N_API ANFPImageAsciiBinaryRecordRemovePosition(HANFPImageAsciiBinaryRecord hRecord, NInt index);
NResult N_API ANFPImageAsciiBinaryRecordClearPositions(HANFPImageAsciiBinaryRecord hRecord);

NResult N_API ANFPImageAsciiBinaryRecordGetPrintPositionCount(HANFPImageAsciiBinaryRecord hRecord, NInt * pValue);
NResult N_API ANFPImageAsciiBinaryRecordGetPrintPosition(HANFPImageAsciiBinaryRecord hRecord, NInt index, struct ANFPrintPosition_ * pValue);
NResult N_API ANFPImageAsciiBinaryRecordGetPrintPositionsEx(HANFPImageAsciiBinaryRecord hRecord, struct ANFPrintPosition_ * arValue, NInt valueLength);
NResult N_API ANFPImageAsciiBinaryRecordSetPrintPosition(HANFPImageAsciiBinaryRecord hRecord, NInt index, const struct ANFPrintPosition_ * pValue);
NResult N_API ANFPImageAsciiBinaryRecordAddPrintPosition(HANFPImageAsciiBinaryRecord hRecord, const struct ANFPrintPosition_ * pValue);
NResult N_API ANFPImageAsciiBinaryRecordInsertPrintPosition(HANFPImageAsciiBinaryRecord hRecord, NInt index, const struct ANFPrintPosition_ * pValue);
NResult N_API ANFPImageAsciiBinaryRecordRemovePrintPosition(HANFPImageAsciiBinaryRecord hRecord, NInt index);
NResult N_API ANFPImageAsciiBinaryRecordClearPrintPositions(HANFPImageAsciiBinaryRecord hRecord);

NResult N_API ANFPImageAsciiBinaryRecordGetQualityMetricCount(HANFPImageAsciiBinaryRecord hRecord, NInt * pValue);
NResult N_API ANFPImageAsciiBinaryRecordGetQualityMetric(HANFPImageAsciiBinaryRecord hRecord, NInt index, struct ANFPQualityMetric_ * pValue);
NResult N_API ANFPImageAsciiBinaryRecordGetQualityMetricsEx(HANFPImageAsciiBinaryRecord hRecord, struct ANFPQualityMetric_ * arValue, NInt valueLength);
NResult N_API ANFPImageAsciiBinaryRecordSetQualityMetric(HANFPImageAsciiBinaryRecord hRecord, NInt index, const struct ANFPQualityMetric_ * pValue);
NResult N_API ANFPImageAsciiBinaryRecordAddQualityMetric(HANFPImageAsciiBinaryRecord hRecord, const struct ANFPQualityMetric_ * pValue);
NResult N_API ANFPImageAsciiBinaryRecordInsertQualityMetric(HANFPImageAsciiBinaryRecord hRecord, NInt index, const struct ANFPQualityMetric_ * pValue);
NResult N_API ANFPImageAsciiBinaryRecordRemoveQualityMetric(HANFPImageAsciiBinaryRecord hRecord, NInt index);
NResult N_API ANFPImageAsciiBinaryRecordClearQualityMetrics(HANFPImageAsciiBinaryRecord hRecord);

NResult N_API ANFPImageAsciiBinaryRecordGetImpressionType(HANFPImageAsciiBinaryRecord hRecord, BdifFPImpressionType * pValue);
NResult N_API ANFPImageAsciiBinaryRecordSetImpressionType(HANFPImageAsciiBinaryRecord hRecord, BdifFPImpressionType value);

#ifdef N_CPP
}
#endif

#endif // !AN_FP_IMAGE_ASCII_BINARY_RECORD_H_INCLUDED
