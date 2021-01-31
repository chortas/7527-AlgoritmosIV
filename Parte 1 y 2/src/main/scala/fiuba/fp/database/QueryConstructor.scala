package fiuba.fp.database

import fiuba.fp.models.DataSetRow
import doobie._
import doobie.implicits._
import doobie.implicits.javatime._

object QueryConstructor {
  def constructInsert(dataSetRow: DataSetRow): doobie.Update0 =
    sql"insert into fptp.dataset (id, date, open, high, low, last, close, dif, curr, o_vol, o_dif, op_vol, unit, dollar_BN, dollar_itau, w_diff, hash_code) values ( ${dataSetRow.id}, ${dataSetRow.date}, ${dataSetRow.open}, ${dataSetRow.high}, ${dataSetRow.low}, ${dataSetRow.last}, ${dataSetRow.close}, ${dataSetRow.diff}, ${dataSetRow.curr}, ${dataSetRow.OVol}, ${dataSetRow.Odiff}, ${dataSetRow.OpVol}, ${dataSetRow.unit}, ${dataSetRow.dollarBN}, ${dataSetRow.dollarItau}, ${dataSetRow.wDiff}, ${dataSetRow.hashCode})".update

  def constructSelect(): doobie.Query0[DataSetRow] =
    sql"select * from fptp.dataset"
      .query[DataSetRow]
}
